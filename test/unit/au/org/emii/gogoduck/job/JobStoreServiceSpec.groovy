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
                    outputFilename: 'aggr.nc',
                    outputPath: 'jobsDirPath'
                ]
            ]
        ]

        expect:
        service.getDir(job) == 'jobsDirPath/asdf'
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
        service.list()

        then:
        1 * service.get('1111') >> null
        1 * service.get('2222') >> null
        1 * service.get('3333') >> null
    }

    def "delete single job"() {
        when:
        service.delete(job)

        then:
        1 * service.rmDir(job) >> null
    }

    def "delete list of jobs"() {
        when:
        service.delete([job, job])

        then:
        2 * service.rmDir(job) >> null
    }
}
