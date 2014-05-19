package au.org.emii.gogoduck.job

import au.org.emii.gogoduck.worker.Worker

class JobExecutorJob {

    def sessionRequired = false
    def concurrent = false

    def grailsApplication
    def jobStoreService
    def notificationService

    def execute(context) {
        run(context.mergedJobDataMap.get('job'))
    }

    def run(job) {
        jobStoreService.save(job)
        getWorker(job).run(successHandler, failureHandler)
    }

    def getWorker(job) {
        return new Worker(
            shellCmd: grailsApplication.config.worker.cmd,
            job: job,
            outputFilename: jobStoreService.getAggrPath(job),
            reportFilename: jobStoreService.getReportPath(job),
            maxGogoduckTimeMinutes: grailsApplication.config.worker.maxGogoduckTimeMinutes,
            fileLimit: grailsApplication.config.worker.fileLimit
        )
    }

    def successHandler = {
        job ->
        notificationService.sendJobSuccessNotification(job)
    }

    def failureHandler = {
        job, errMsg ->
        notificationService.sendJobFailureNotification(job)
    }
}
