package au.org.emii.gogoduck.job

import grails.plugin.mail.MailService
import grails.test.mixin.*
import spock.lang.Specification
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

import au.org.emii.gogoduck.test.TestHelper
import au.org.emii.gogoduck.worker.Worker

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {

    def job
    def jobStoreService
    def mailService
    def messageSource
    def worker

    def setup() {
        job = TestHelper.createJob()
        job.uuid = '1234'
        jobStoreService = Mock(JobStoreService)
        service.jobStoreService = jobStoreService
        mailService = Mock(MailService)
        service.mailService = mailService
        messageSource = Mock(MessageSource)
        service.messageSource = messageSource

        worker = Mock(Worker)

        service.metaClass.getWorker = {
            worker
        }
    }

    def "run sends 'job registered' notification"() {
        given:
        def sendCalled = false
        service.metaClass.sendJobRegisteredNotification = {
            theJob ->
            sendCalled = (theJob == job)
        }

        when:
        service.run(job)

        then:
        sendCalled
    }

    def "registered notification sends email"() {
        when:
        service.sendJobRegisteredNotification(job)

        then:
        1 * mailService.sendMail(!null)
    }

    def "registered notification subject"() {
        when:
        service.getRegisteredNotificationSubject(job)

        then:
        1 * messageSource.getMessage(
            'job.registered.subject',
            ['1234'].toArray(),
            LocaleContextHolder.locale
        )
    }

    def "registered notification body"() {
        when:
        service.getRegisteredNotificationBody(job)

        then:
        1 * messageSource.getMessage(
            'job.registered.body',
            ['1234'].toArray(),
            LocaleContextHolder.locale
        )
    }

    def "makes job dir, writes job as json to file"() {
        when:
        service.run(job)

        then:
        1 * jobStoreService.makeDir(job)
        1 * jobStoreService.writeToFileAsJson(job)
    }

    def "runs worker"() {
        when:
        service.run(job)

        then:
        1 * worker.run()
    }

    def "run sends 'job success' notification"() {
        given:
        def sendCalled = false
        service.metaClass.sendJobSuccessNotification = {
            theJob ->
            sendCalled = (theJob == job)
        }

        when:
        service.run(job)

        then:
        sendCalled
    }

    def "success notification subject"() {
        when:
        service.getSuccessNotificationSubject(job)

        then:
        1 * messageSource.getMessage(
            'job.success.subject',
            ['1234'].toArray(),
            LocaleContextHolder.locale
        )
    }

    def "success notification body"() {
        when:
        job.metaClass.getAggrUrl = {
            "http://thejob/1234"
        }
        service.getSuccessNotificationBody(job)

        then:
        1 * messageSource.getMessage(
            'job.success.body',
            ['1234', 'http://thejob/1234'].toArray(),
            LocaleContextHolder.locale
        )
    }
}
