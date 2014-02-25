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
        jobStoreService.makeDir(job)
        jobStoreService.writeToFileAsJson(job)
        getWorker(job).run()
        notificationService.sendJobSuccessNotification(job)
    }

    def getWorker(job) {
        return new Worker(
            shellCmd: grailsApplication.config.worker.cmd,
            job: job,
            outputFilename: jobStoreService.getAggrPath(job),
            fileLimit: grailsApplication.config.worker.fileLimit
        )
    }
}