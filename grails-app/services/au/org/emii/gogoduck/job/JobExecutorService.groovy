package au.org.emii.gogoduck.job

import au.org.emii.gogoduck.worker.Worker

class JobExecutorService {

    def grailsApplication
    def jobStoreService
    def mailService

    def run(job) {
        jobStoreService.makeDir(job)
        jobStoreService.writeToFileAsJson(job)
        getWorker(job).run()
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