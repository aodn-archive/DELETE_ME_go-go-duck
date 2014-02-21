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
}
