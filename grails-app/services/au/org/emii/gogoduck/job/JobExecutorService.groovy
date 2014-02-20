package au.org.emii.gogoduck.job

import au.org.emii.gogoduck.worker.Worker

class JobExecutorService {

    def grailsApplication

    def run(job) {
        getWorker(job).run()
    }

    def getWorker(job) {
        return new Worker(
            shellCmd: grailsApplication.config.worker.cmd,
            job: job,
            outputFilename: grailsApplication.config.worker.outputFilename,
            fileLimit: grailsApplication.config.worker.fileLimit
        )
    }
}