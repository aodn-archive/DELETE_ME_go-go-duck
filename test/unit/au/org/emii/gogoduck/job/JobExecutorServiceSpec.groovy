package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification
import java.util.concurrent.BlockingQueue

import au.org.emii.gogoduck.test.TestHelper

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {

    def job
    def jobStoreService
    def notificationService

    def setup() {
        job = TestHelper.createJob()
        job.uuid = '1234'
        jobStoreService = Mock(JobStoreService)
        notificationService = Mock(NotificationService)

        service.jobStoreService = jobStoreService
        service.notificationService = notificationService
    }

    def "register sends 'job registered' notification"() {
        when:
        service.register(job)

        then:
        1 * notificationService.sendJobRegisteredNotification(job)
    }

    def "register saves job"() {
        when:
        service.register(job)

        then:
        1 * jobStoreService.save(job)
    }
}
