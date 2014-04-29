package au.org.emii.gogoduck.aggr
import org.joda.time.*
import org.joda.time.format.*

class AggrController {

    static allowedMethods = [show: "GET"]
    def jobStoreService

    def show() {
        log.debug("params: ${params}")

        def job = jobStoreService.get(params.uuid)
        log.debug("job: ${job}")

        def aggrFile = jobStoreService.getAggrFile(job)

        if (aggrFile) {
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "filename=${getFilenameToServe(job)}")
            response.outputStream.write(aggrFile.bytes)
            response.outputStream.flush()
        }
        else {
            render(status: 500, text: "No aggregration file for id '${params.id}'.")
        }
    }

    def getFilenameToServe(job) {
        DateTime jobDateTime = new DateTime(job.createdTimestamp)
        String jobDateTimeFormattedForFilename = ISODateTimeFormat.basicDateTime().print(jobDateTime)
        "IMOS-aggregation-" + jobDateTimeFormattedForFilename + ".nc"
    }
}
