package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper

@TestFor(JobController)
class JobControllerSpec extends Specification {

    def "save with valid request registers job"() {
        given:
        def job = TestHelper.createJob()
        JobExecutorService jobExecutorService = Mock()
        controller.jobExecutorService = jobExecutorService

        when:
        controller.save(job)

        then:
        response.status == 200
        1 * jobExecutorService.register(job)
    }
}
