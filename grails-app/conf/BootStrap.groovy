import au.org.emii.gogoduck.job.CleanupJob
import au.org.emii.gogoduck.job.Job

class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        Job.metaClass.getServerURL = {
            grailsApplication.config.grails.serverURL
        }

        CleanupJob.schedule(grailsApplication.config.job.cleanup.trigger)
    }

    def destroy = {
    }
}
