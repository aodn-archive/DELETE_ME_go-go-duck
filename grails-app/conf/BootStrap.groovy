import au.org.emii.gogoduck.job.CleanupJob
import au.org.emii.gogoduck.job.Job

class BootStrap {

    def grailsApplication

    def init = { servletContext ->

        log.info("App version: ${grailsApplication.metadata['app.version']}, " +
                 "build number: ${grailsApplication.metadata['app.buildNumber']}")

        Job.metaClass.getServerURL = {
            grailsApplication.config.grails.serverURL
        }

        CleanupJob.schedule(grailsApplication.config.job.cleanup.trigger)
    }

    def destroy = {
    }
}
