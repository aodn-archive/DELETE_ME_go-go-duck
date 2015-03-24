package au.org.emii.gogoduck.job

/**
 * Adds in a few extra useful attributes for a client.
 */
class JobPresenter extends HashMap {
    JobPresenter(job, jobExecutorService, jobStoreService, createLink) {
        super(job.properties)

        if (job.status == Status.NEW) {
            put('queuePosition', jobExecutorService.getQueuePosition(job))
        }

        if (job.status == Status.SUCCEEDED) {
            put('aggrUrl', createLink(controller: 'aggr', action: 'show', id: job.uuid, absolute: true))
        }

        if (job.status == Status.SUCCEEDED || job.status == Status.FAILED) {
            put('report', jobStoreService.getReport(job))
        }
    }
}
