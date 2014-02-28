package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper

@TestFor(JobStoreService)
class JobStoreServiceSpec extends Specification {

    def "get job directory path"() {
        given:
        def job = TestHelper.createJob()
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
        given:
        def job = TestHelper.createJob()
        def service = Spy(JobStoreService)

        when:
        service.save(job)

        then:
        1 * service.makeDir(job) >> null
        1 * service.writeToFileAsJson(job) >> null
    }
}
