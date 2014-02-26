package au.org.emii.gogoduck.job

class JobController {

    static allowedMethods = [ save: "POST" ]

    def jobExecutorService

    def save(Job job) {
        if (job.hasErrors()) {
            render(status: 400, template: "error", model: [job: job])
        }
        else {
            jobExecutorService.run(job)
            render(status: 200, text: job.toJsonString())
        }
    }
}
