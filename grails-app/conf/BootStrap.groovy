import au.org.emii.gogoduck.job.CleanupJob
import au.org.emii.gogoduck.job.Job
import grails.converters.JSON

class BootStrap {

    def grailsApplication

    def init = { servletContext ->

        log.info("App version: ${grailsApplication.metadata['app.version']}, " +
                 "build number: ${grailsApplication.metadata['app.buildNumber']}")

        Job.metaClass.getServerUrl = {
            grailsApplication.config.grails.serverURL
        }

        JSON.registerObjectMarshaller(Enum) {
            Enum someEnum ->
            someEnum.toString()
        }

        CleanupJob.schedule(grailsApplication.config.job.cleanup.trigger)
    }
}
