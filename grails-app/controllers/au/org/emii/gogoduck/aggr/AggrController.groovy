package au.org.emii.gogoduck.aggr

class AggrController {

    static allowedMethods = [ show: "GET" ]
    def jobStoreService

    def show() {
        log.debug("params: ${params}")

        def job = jobStoreService.getJob(params.uuid)
        log.debug("job: ${job}")


        def aggrFile = jobStoreService.getAggrFile(job)

        if (aggrFile) {
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "filename=${aggrFile.name}")
            response.outputStream.write(aggrFile.bytes)
            response.outputStream.flush()
        }
        else {
            render(status: 500, text: "No aggregration file for id '${params.id}'.")
        }
    }
}