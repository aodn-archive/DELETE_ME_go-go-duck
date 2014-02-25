import au.org.emii.gogoduck.job.Job

class BootStrap {

    def grailsApplication

    def init = { servletContext ->
        Job.metaClass.getGrailsApplication = {
            ->
            grailsApplication
        }
    }

    def destroy = {
    }
}
