package au.org.emii.gogoduck.job

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
        render jobStoreService.get(params.uuid)
    }
}
