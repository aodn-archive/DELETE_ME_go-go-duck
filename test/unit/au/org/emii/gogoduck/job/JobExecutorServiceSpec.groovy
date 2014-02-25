package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {

    def job
    def jobExecutorJob
    def notificationService

    def setup() {
        job = TestHelper.createJob()
        job.uuid = '1234'
        jobExecutorJob = Mock(JobExecutorJob)
        service.jobExecutorJob = jobExecutorJob
        notificationService = Mock(NotificationService)
        service.notificationService = notificationService
    }

    def "run sends 'job registered' notification"() {
        when:
        service.run(job)

        then:
        1 * notificationService.sendJobRegisteredNotification(job)
    }

    def "calls run on job executor job"() {
        when:
        service.run(job)

        then:
        1 * jobExecutorJob.run(job)
    }
}
