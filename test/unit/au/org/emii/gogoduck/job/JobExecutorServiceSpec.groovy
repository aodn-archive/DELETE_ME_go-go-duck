package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import au.org.emii.gogoduck.test.TestHelper

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {

    def grailsLinkGenerator
    def job
    def jobStoreService
    def notificationService

    def setup() {
        job = TestHelper.createJob()
        job.uuid = '1234'
        jobStoreService = Mock(JobStoreService)
        notificationService = Mock(NotificationService)

        grailsLinkGenerator = Mock(LinkGenerator)
        grailsLinkGenerator.link() >> "http://someUrl"

        service.grailsLinkGenerator = grailsLinkGenerator
        service.jobStoreService = jobStoreService
        service.notificationService = notificationService
        service.metaClass.newWorker = {
            [ run: { success, failure -> } ]
        }
        service.metaClass.getPresentedJob = {
            job
        }
    }

    def "register sends 'job registered' notification"() {
        when:
        service.register(job)

        then:
        1 * notificationService.sendJobRegisteredNotification(job)
    }

    def "register saves job, sets status to NEW"() {
        when:
        service.register(job)

        then:
        1 * jobStoreService.save(job)
        job.status == Status.NEW
        job.reason == Reason.NONE
    }

    def "run runs worker, sets status to IN_PROGRESS"() {
        when:
        service.run(job)

        then:
        1 * jobStoreService.save(job)
        job.status == Status.IN_PROGRESS
        job.reason == Reason.NONE
    }

    def "success handler sends 'job success' notification, sets status to SUCCEEDED"() {
        when:
        service.successHandler(job)

        then:
        1 * notificationService.sendJobSuccessNotification(job)
        1 * jobStoreService.save(job)
        job.status == Status.SUCCEEDED
        job.reason == Reason.NONE
    }

    def "failed handler sends 'job failure' notification, sets status to FAILED"() {
        when:
        service.failureHandler(job, Reason.TIMEOUT_EXPIRED)

        then:
        1 * notificationService.sendJobFailureNotification(job)
        1 * jobStoreService.save(job)
        job.status == Status.FAILED
        job.reason == Reason.TIMEOUT_EXPIRED
    }
}
