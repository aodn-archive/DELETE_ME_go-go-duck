package au.org.emii.gogoduck.job

class JobController {

    static allowedMethods = [ save: "POST" ]

    def jobExecutorService

    def save(Job job) {
        if (job.hasErrors()) {
            log.info "Could not register job ($job). Errors: ${job.errors}"

            render(status: 400, template: "error", model: [job: job])
        }
        else {
            jobExecutorService.run(job)
            render(status: 200, text: job.toJsonString())
        }
    }
}
