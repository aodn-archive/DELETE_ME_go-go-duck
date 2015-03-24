package au.org.emii.gogoduck.job

import grails.plugin.mail.MailService
import grails.test.mixin.*
import spock.lang.Specification
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

import au.org.emii.gogoduck.test.TestHelper

@TestFor(NotificationService)
class NotificationServiceSpec extends Specification {

    def job
    def grailsLinkGenerator
    def mailService
    def messageSource

    def setup() {
        job = TestHelper.createJob()
        job.uuid = '1234'

        grailsLinkGenerator = Mock(LinkGenerator)
        grailsLinkGenerator.link() >> "http://someUrl"

        service.grailsLinkGenerator = grailsLinkGenerator
        mailService = Mock(MailService)
        service.mailService = mailService
        messageSource = Mock(MessageSource)
        service.messageSource = messageSource
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
            ['1234', service.getJobUrl(job)].toArray(),
            LocaleContextHolder.locale
        )
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
        service.getSuccessNotificationBody(job)

        then:
        1 * messageSource.getMessage(
            'job.success.body',
            ['1234', service.getAggrUrl(job)].toArray(),
            LocaleContextHolder.locale
        )
    }

    def "failure notification subject"() {
        when:
        service.getFailureNotificationSubject(job)

        then:
        1 * messageSource.getMessage(
            'job.failure.subject',
            ['1234'].toArray(),
            LocaleContextHolder.locale
        )
    }

    def "failure notification body"() {
        when:
        service.getFailureNotificationBody(job)

        then:
        1 * messageSource.getMessage(
            'job.failure.body',
            ['1234', service.getAggrUrl(job)].toArray(),
            LocaleContextHolder.locale
        )
    }

    def "url generation"() {
        when:
        service.getJobUrl([uuid: 123])
        service.getAggrUrl([uuid: 456])

        then:
        1 * grailsLinkGenerator.link([controller: 'job', action: 'show', id: 123, absolute: true])
        1 * grailsLinkGenerator.link([controller: 'aggr', action: 'show', id: 456, absolute: true])
    }
}
