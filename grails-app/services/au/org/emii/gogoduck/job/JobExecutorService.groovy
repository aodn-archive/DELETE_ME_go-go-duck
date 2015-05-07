package au.org.emii.gogoduck.job

import au.org.emii.gogoduck.worker.Worker


class JobExecutorService {

    static final JobQueue JOB_QUEUE = new JobQueue()

    def grailsApplication
    def grailsLinkGenerator
    def jobStoreService
    def notificationService

    static {
        new Thread(JOB_QUEUE).start()
    }

    static void clearQueue() {
        JOB_QUEUE.clear()
    }

    def register(job) {
        setJobStatusAndSave(job, Status.NEW)
        notificationService.sendJobRegisteredNotification(getPresentedJob(job))

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

    def getQueuePosition(job) {
        return JOB_QUEUE.getQueuePosition(job)
    }

    def successHandler = {
        job ->

        setJobStatusAndSave(job, Status.SUCCEEDED)
        notificationService.sendJobSuccessNotification(getPresentedJob(job))
    }

    def failureHandler = {
        job, reason ->

        setJobStatusAndSave(job, Status.FAILED, reason)
        notificationService.sendJobFailureNotification(getPresentedJob(job))
    }

    def setJobStatusAndSave(job, status, reason = Reason.NONE) {
        job.status = status
        job.reason = reason
        jobStoreService.save(job)
    }

    def getPresentedJob(job) {
        new JobPresenter(job, this, jobStoreService, { grailsLinkGenerator.link(it) } )
    }
}
