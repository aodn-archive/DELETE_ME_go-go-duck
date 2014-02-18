package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

@TestFor(JobController)
class JobControllerSpec extends Specification {

    def "save with valid request"() {

        given:
        def job = createJob()

        when:
        controller.save(job)

        then:
        response.status == 200
    }

    def createJob() {
        return new Job(
            emailAddress: 'gogo@duck.com',
            temporalExtent: [
                start: '2013-11-20T00:30:00.000Z',
                end:   '2013-11-20T10:30:00.000Z'
            ],
            spatialExtent: [
                north: '-32.150743',
                south: '-33.433849',
                east:  '114.15197',
                west:  '115.741219'
            ]
        )
    }
}