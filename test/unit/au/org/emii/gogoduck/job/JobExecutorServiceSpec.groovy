package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {

    def job
    def notificationService
    def triggerNowCalled

    def setup() {
        job = TestHelper.createJob()
        job.uuid = '1234'
        notificationService = Mock(NotificationService)
        service.notificationService = notificationService

        triggerNowCalled = false
        JobExecutorJob.metaClass.static.triggerNow = {
            triggerNowCalled = (it == [job: job])
        }
    }

    def "run sends 'job registered' notification"() {
        when:
        service.run(job)

        then:
        1 * notificationService.sendJobRegisteredNotification(job)
    }

    def "triggers job executor job"() {
        when:

        service.run(job)

        then:
        triggerNowCalled
    }
}
