package au.org.emii.gogoduck.job

class JobController {

    static allowedMethods = [ save: "POST" ]

    def save(Job job) {
        if (job.hasErrors()) {
            render (status: 400, text: "Invalid request format: ${job.errors}")
        }
        else {
            render (status: 200)
        }
    }
}
