package au.org.emii.gogoduck

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(JobCleanupController)
class JobCleanupControllerSpec extends Specification {
    def setup() {
    }

    def "prettySize"() {
        given:
        def jobCleanup = new JobCleanupController()
        when:
        def res = jobCleanup._prettySize(20)
        then:
        res == "20B"
    }

    void "test something"() {
    }
}