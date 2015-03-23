package au.org.emii.gogoduck.job

import au.org.emii.gogoduck.worker.Worker

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.BlockingQueue

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class JobExecutorService {

    static BlockingQueue<Map> JOB_QUEUE

    def grailsApplication
    def jobStoreService
    def notificationService

    static {
        // Note: this queue is unbounded - but this is the same as the previous behaviour
        // using the quartz scheduler.
        JOB_QUEUE = new LinkedBlockingQueue<Map>()

        final Logger log = LoggerFactory.getLogger(JobExecutorService)

        new Thread(
            new Runnable() {
                void run() {
                    while (true) {
                        try {
                            log.debug "Waiting for job..."
                            def jobContext = JOB_QUEUE.take()
                            def job = jobContext.job
                            def executor = jobContext.executor

                            log.debug "Job taken, queue size: ${JOB_QUEUE.size()}"

                            executor.run(job)
                        }
                        catch (InterruptedException e) {
                            log.error("Error handling job", e)
                        }
                    }
                }
            }
        ).start()
    }

    def register(job) {
        setJobStatusAndSave(job, Status.NEW)
        notificationService.sendJobRegisteredNotification(job)

        JOB_QUEUE.offer([job: job, executor: this])
        log.debug "job offered, queue size: ${JOB_QUEUE.size()}"
    }

    def run(job) {
        setJobStatusAndSave(job, Status.IN_PROGRESS)
        newWorker(job).run(successHandler, failureHandler)
    }

    def newWorker(job) {
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

        setJobStatusAndSave(job, Status.SUCCEEDED)
        notificationService.sendJobSuccessNotification(job)
    }

    def failureHandler = {
        job ->

        setJobStatusAndSave(job, Status.FAILED)
        notificationService.sendJobFailureNotification(job)
    }

    def setJobStatusAndSave(job, status) {
        job.status = status
        jobStoreService.save(job)
    }
}
