package au.org.emii.gogoduck.aggr
import org.joda.time.*
import org.joda.time.format.*

class AggrController {

    static allowedMethods = [show: "GET"]
    def jobStoreService
    def grailsApplication

    def show() {
        log.debug("params: ${params}")

        def job = jobStoreService.get(params.id)
        log.debug("job: ${job}")

        def aggrFile = jobStoreService.getAggrFile(job)

        if (aggrFile) {
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "filename=${filenameToServe(job)}")
            response.outputStream << aggrFile.newInputStream()
        }
        else {
            render(status: 500, text: "No aggregration file for id '${params.id}'.")
        }
    }

    String filenameToServe(job) {
        DateTime jobDateTime = new DateTime(job.createdTimestamp)
        String jobDateTimeFormattedForFilename = ISODateTimeFormat.basicDateTime().print(jobDateTime)
        return String.format(
            "%s%s.nc",
            grailsApplication.config.worker.outputFilenamePrefixForUser,
            jobDateTimeFormattedForFilename
        )
    }
}
