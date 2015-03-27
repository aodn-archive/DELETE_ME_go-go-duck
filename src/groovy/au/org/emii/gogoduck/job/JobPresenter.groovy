package au.org.emii.gogoduck.job

/**
 * Adds in a few extra useful attributes for a client.
 */
class JobPresenter extends HashMap {
    JobPresenter(job, jobExecutorService, jobStoreService, createLink) {
        super(job.properties)

        switch (job.status) {
            case (Status.NEW):
                addQueuePosition(job, jobExecutorService)
                break

            case (Status.SUCCEEDED):
                addAggrUrl(job, createLink)
                addReport(job, jobStoreService)
                break

            case (Status.FAILED):
                addReport(job, jobStoreService)
                break
        }
    }

    void addQueuePosition(job, jobExecutorService) {
        put('queuePosition', jobExecutorService.getQueuePosition(job) + 1)
    }

    void addAggrUrl(job, createLink) {
        put('aggrUrl', createLink(controller: 'aggr', action: 'show', id: job.uuid, absolute: true))
    }

    void addReport(job, jobStoreService) {
        put('report', jobStoreService.getReport(job))
    }
}
