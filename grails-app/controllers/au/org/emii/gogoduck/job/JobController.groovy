package au.org.emii.gogoduck.job

import grails.converters.JSON

class JobController {

    static allowedMethods = [ save: "POST", show: "GET" ]

    def jobExecutorService
    def jobStoreService

    def save(Job job) {
        if (job.hasErrors()) {
            log.info "Could not register job ($job). Errors: ${job.errors}"

            render(status: 400, template: "error", model: [job: job])
        }
        else {
            jobExecutorService.register(job)
            render(status: 200, text: job.toJsonString())
        }
    }

    def show() {
        def job = jobStoreService.get(params.id)

        if (!job) {
            render(status: 404)
            return
        }

        render(job.properties + [ queuePosition:  jobExecutorService.getQueuePosition(job) ] as JSON)
    }
}
