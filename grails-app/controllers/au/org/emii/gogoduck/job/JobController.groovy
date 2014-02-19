package au.org.emii.gogoduck.job

class JobController {

    static allowedMethods = [ save: "POST" ]

    def jobExecutorService

    def save(Job job) {

        log.info("job: ${job.dump()}")
        log.info("job: ${job.subsetDescriptor.dump()}")

        if (job.hasErrors()) {
            render (status: 400, text: "Invalid request format: ${job.errors}")
        }
        else {
            jobExecutorService.run(job)
            render (status: 200)
        }
    }
}
