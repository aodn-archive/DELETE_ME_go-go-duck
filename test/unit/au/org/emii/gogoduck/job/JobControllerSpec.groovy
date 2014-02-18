package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

@TestFor(JobController)
class JobControllerSpec extends Specification {

    def "save with valid request"() {

        given:
        setupValidParams()

        when:
        controller.save()

        then:
        response.status == 200
    }

    def "save with missing email address"() {

        given:
        setupValidParams()
        controller.params.remove('emailAddress')

        when:
        controller.save()

        then:
        expectBadRequestWithMessage "'emailAddress' must be specified."
    }

    def "save with missing temporal extent"() {

        given:
        setupValidParams()
        controller.params.remove('temporalExtent')

        when:
        controller.save()

        then:
        expectBadRequestWithMessage "'temporalExtent' must be specified."
    }

    def "save with missing spatial extent"() {

        given:
        setupValidParams()
        controller.params.remove('spatialExtent')

        when:
        controller.save()

        then:
        expectBadRequestWithMessage "'spatialExtent' must be specified."
    }

    void setupValidParams() {
        controller.params.emailAddress = 'gogo@duck.com'
        controller.params.temporalExtent = [
            start: '2013-11-20T00:30:00.000Z',
            end:   '2013-11-20T10:30:00.000Z'
        ]
        controller.params.spatialExtent = [
            north: '-32.150743',
            south: '-33.433849',
            east:  '114.15197',
            west:  '115.741219'
        ]
    }

    void expectBadRequestWithMessage(msg) {
        assert response.status == 400
        assert response.text == "Invalid request format: ${msg}"
    }
}