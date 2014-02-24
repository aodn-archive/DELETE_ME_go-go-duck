package au.org.emii.gogoduck.job

import org.springframework.context.i18n.LocaleContextHolder
import au.org.emii.gogoduck.worker.Worker

class JobExecutorService {

    def grailsApplication
    def jobStoreService
    def mailService
    def messageSource

    def run(job) {
        sendJobRegisteredNotification(job)
        jobStoreService.makeDir(job)
        jobStoreService.writeToFileAsJson(job)
        getWorker(job).run()
        sendJobSuccessNotification(job)
    }

    def sendJobRegisteredNotification(job) {
        mailService.sendMail {
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

    def getWorker(job) {
        return new Worker(
            shellCmd: grailsApplication.config.worker.cmd,
            job: job,
            outputFilename: jobStoreService.getAggrPath(job),
            fileLimit: grailsApplication.config.worker.fileLimit
        )
    }

    def sendJobSuccessNotification(job) {
        mailService.sendMail {
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
}