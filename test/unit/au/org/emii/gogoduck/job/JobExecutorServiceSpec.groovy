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

    // TODO: cannot get this (or something like it) to work.
    // Leaving it here for now in case someone else wants to have a try, otherwise,
    // I'll make sure it's gone before this current parcel of work is complete.
    // def "register queues job"() {
    //     given:
    //     JobExecutorService.JOB_QUEUE = Mock(BlockingQueue)

    //     when:
    //     service.register(job)

    //     then:
    //     1 * JobExecutorService.JOB_QUEUE.offer(job)
    // }
}
