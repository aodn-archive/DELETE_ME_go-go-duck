package au.org.emii.gogoduck.job

import org.springframework.context.i18n.LocaleContextHolder

class NotificationService {
    def mailService
    def messageSource
    def jobStoreService

    def sendJobRegisteredNotification(job) {
        sendMailAndLog {
            to job.emailAddress.toString()
            subject getRegisteredNotificationSubject(job)
            body getRegisteredNotificationBody(job)
        }
    }

    def getRegisteredNotificationSubject(job) {
        getMessage('job.registered.subject', [job.uuid])
    }

    def getRegisteredNotificationBody(job) {
        getMessage('job.registered.body', [job.uuid])
    }

    def sendJobSuccessNotification(job) {
        def jobAggregationReport = new File(jobStoreService.getReportPath(job))
        log.info("Sending success email to '${job.emailAddress.toString()}' with report from '${jobAggregationReport.path}'")

        sendMailAndLog {
            multipart true
            to job.emailAddress.toString()
            subject getSuccessNotificationSubject(job)
            body getSuccessNotificationBody(job)
            attachBytes "aggregation_report.txt", "text/plain", jobAggregationReport.readBytes()
        }
    }

    def getSuccessNotificationSubject(job) {
        getMessage('job.success.subject', [job.uuid])
    }

    def getSuccessNotificationBody(job) {
        getMessage('job.success.body', [job.uuid, job.aggrUrl])
    }

    def sendJobFailureNotification(job) {
        def jobAggregationReport = new File(jobStoreService.getReportPath(job))
        log.info("Sending failure email to '${job.emailAddress.toString()}' with report from '${jobAggregationReport.path}'")

        sendMailAndLog {
            multipart true
            to job.emailAddress.toString()
            subject getFailureNotificationSubject(job)
            body getFailureNotificationBody(job)
            attachBytes "aggregation_report.txt", "text/plain", jobAggregationReport.readBytes()
        }
    }

    def getFailureNotificationSubject(job) {
        getMessage('job.failure.subject', [job.uuid])
    }

    def getFailureNotificationBody(job) {
        getMessage('job.failure.body', [job.uuid, job.aggrUrl])
    }

    def getMessage(messageKey, messageParams = []) {
        messageSource.getMessage(
            messageKey,
            messageParams.toArray(),
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
