package au.org.emii.gogoduck.aggr

import au.org.emii.gogoduck.job.*

class AggrController {

    static allowedMethods = [ show: "GET" ]
    def jobStoreService

    def show() {
        log.debug("params: ${params}")

        def aggrFile = jobStoreService.getAggrFile(params.id)

        log.debug("aggrFile: ${aggrFile}")

        if (aggrFile) {
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "filename=${aggrFile.name}")
            response.outputStream.write(aggrFile.bytes)
            response.outputStream.flush()
        }
        else {
            render (status: 500, text: "No aggregration file for id '${params.id}'.")
        }
    }
}