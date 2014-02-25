package au.org.emii.gogoduck.job

import au.org.emii.gogoduck.worker.Worker

class JobExecutorService {

    def grailsApplication
    def jobStoreService
    def notificationService

    def run(job) {
        notificationService.sendJobRegisteredNotification(job)
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