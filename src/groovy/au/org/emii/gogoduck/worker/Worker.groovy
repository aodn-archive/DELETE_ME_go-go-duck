package au.org.emii.gogoduck.worker

import org.apache.commons.io.IOUtils

import au.org.emii.gogoduck.job.Job
import grails.converters.JSON

class Worker {
    Job job
    Closure shellCmd
    String outputFilename
    Integer fileLimit

    void run(successHandler, failureHandler) {

        try {
            def process = execute(getCmd())
            log.info("worker output: ${IOUtils.toString(process.getInputStream(), 'UTF-8')}")

            if (process.exitValue() == 0) {
                successHandler(job)
            }
            else {
                String errMsg = IOUtils.toString(process.getErrorStream(), 'UTF-8')
                log.error("Worker failed: ${errMsg}")
                failureHandler(job, errMsg)
            }
        }
        catch (IOException e) {
            log.error('Worker failed', e)
            failureHandler(job, e.message)
        }
    }

    def getCmd() {

        def temporalExtent = job.subsetDescriptor.temporalExtent
        def spatialExtent = job.subsetDescriptor.spatialExtent
        def cmdOptions = String.format(
            '-p %s -s TIME,%s,%s;LATITUDE,%s,%s;LONGITUDE,%s,%s -o %s -l %s',
            job.layerName,
            temporalExtent.start,
            temporalExtent.end,
            spatialExtent.south,
            spatialExtent.north,
            spatialExtent.west,
            spatialExtent.east,
            outputFilename,
            fileLimit
        )

        shellCmd.call(cmdOptions)
    }

    Process execute(cmd) {
        log.info("Executing command: '${cmd}'")

        def proc = cmd.execute()
        proc.waitFor()

        return proc
    }
}