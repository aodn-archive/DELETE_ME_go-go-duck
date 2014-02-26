package au.org.emii.gogoduck.job

import org.springframework.context.i18n.LocaleContextHolder

class NotificationService {
    def mailService
    def messageSource

    def sendJobRegisteredNotification(job) {
        sendMailAndLog {
            to job.emailAddress.toString()
            subject getRegisteredNotificationSubject(job)
            body getRegisteredNotificationBody(job)
        }
    }

    def getRegisteredNotificationSubject(job) {
        messageSource.getMessage(
            'job.registered.subject',
            [job.uuid].toArray(),
            LocaleContextHolder.locale
        )
    }

    def getRegisteredNotificationBody(job) {
        messageSource.getMessage(
            'job.registered.body',
            [job.uuid].toArray(),
            LocaleContextHolder.locale
        )
    }

    def sendJobSuccessNotification(job) {
        sendMailAndLog {
            to job.emailAddress.toString()
            subject getSuccessNotificationSubject(job)
            body getSuccessNotificationBody(job)
        }
    }

    def getSuccessNotificationSubject(job) {
        messageSource.getMessage(
            'job.success.subject',
            [job.uuid].toArray(),
            LocaleContextHolder.locale
        )
    }

    def getSuccessNotificationBody(job) {
        messageSource.getMessage(
            'job.success.body',
            [job.uuid, job.aggrUrl].toArray(),
            LocaleContextHolder.locale
        )
    }

    def sendMailAndLog(Closure callable) {
        if (log.isDebugEnabled()) {
            callable.delegate = this
            callable.call()
        }

        mailService.sendMail callable
    }

    def methodMissing(String name, args) {
        log.debug("sending email, ${name}: ${args}")
    }
}