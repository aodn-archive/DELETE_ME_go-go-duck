package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper

// Hack to load converters:
// http://grails.1312388.n4.nabble.com/Error-using-as-JSON-converter-in-a-service-in-a-unit-test-tp4637433p4637457.html
@TestFor(JobController)
class JobStoreServiceSpec extends Specification {

    def job
    def service

    def setup() {
        job = TestHelper.createJob()
        service = Spy(JobStoreService)
    }

    def "get job directory path"() {
        given:
        job.uuid = 'asdf'
        service.grailsApplication = [
            config: [
                worker: [
                    outputFilename: 'aggr-',
                    outputPath: 'jobsDirPath'
                ]
            ]
        ]

        expect:
        service.getPath(job) == 'jobsDirPath/asdf'
    }

    def "get job report path"() {
        given:
        job.uuid = 'asdf'
        service.grailsApplication = [
            config: [
                worker: [
                    outputFilename: 'output.nc',
                    reportFilename: 'report.txt',
                    outputPath: 'jobsDirPath'
                ]
            ]
        ]

        expect:
        service.getReportPath(job) == 'jobsDirPath/asdf/report.txt'
    }

    def "get invalid job"() {
        given:
        service.getJsonPathForId('1234') >> 'jobs/1234/job.json'
        1 * service.getFile('jobs/1234/job.json') >> {
            throw new FileNotFoundException('jobs/1234/job.json (Not asda directory)')
        }

        when:
        true

        then:
        service.get('1234') == null
    }

    def "save makes dir, writes json"() {
        when:
        service.save(job)

        then:
        1 * service.makeDir(job) >> null
        1 * service.writeToFileAsJson(job) >> null
    }

    def "list"() {
        given:
        // TODO: why cannot use: 1 * service.listUuids() >> ['1111', '2222', '3333']
        service.metaClass.listUuids = {
            ['1111', '2222', '3333']
        }

        when:
        def jobs = service.list()

        then:
        1 * service.get('1111') >> job
        1 * service.get('2222') >> null
        1 * service.get('3333') >> job
        jobs == [job, job]
    }

    def "delete single job"() {
        when:
        service.delete(job)

        then:
        1 * service.rmDir(job) >> null
    }

    def "delete list of jobs"() {
        when:
        service.delete([job, null, job])

        then:
        2 * service.rmDir(job) >> null
    }
}
