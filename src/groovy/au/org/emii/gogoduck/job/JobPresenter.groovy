package au.org.emii.gogoduck.job

import grails.converters.JSON

/**
 * Adds in a few extra useful attributes for a client.
 */
class JobPresenter extends HashMap {
    JobPresenter(job, jobExecutorService, jobStoreService, createLink) {
        super(job.properties)

        addUrl(job, createLink)

        switch (job.status) {
            case (Status.NEW):
                addQueuePosition(job, jobExecutorService)
                break

            case (Status.IN_PROGRESS):
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

    void addUrl(job, createLink) {
        put('url', createLink(controller: 'job', action: 'show', id: job.uuid, absolute: true))
    }

    void addAggrUrl(job, createLink) {
        put('aggrUrl', createLink(controller: 'aggr', action: 'show', id: job.uuid, absolute: true))
    }

    void addReport(job, jobStoreService) {
        put('report', jobStoreService.getReport(job))
    }

    public String toJsonString() {
        def propertiesToSerialize = [ 'url', 'queuePosition', 'status' ]
        return this.subMap(propertiesToSerialize) as JSON
    }
}
