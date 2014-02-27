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
        def process = execute(getCmd())

        if (process.exitValue() == 0) {
            successHandler(job)
        }
        else {
            failureHandler(job, IOUtils.toString(process.getErrorStream(), 'UTF-8'))
        }
    }

    def getCmd() {
        def cmdOptions = String.format(
            '-p %1s -s "TIME,%2s,%3s;LATITUDE,%4s,%5s;LONGITUDE,%6s,%7s" -o %8s -l %9$1s',
            job.layerName,
            job.subsetDescriptor.temporalExtent.start,
            job.subsetDescriptor.temporalExtent.end,
            job.subsetDescriptor.spatialExtent.south,
            job.subsetDescriptor.spatialExtent.north,
            job.subsetDescriptor.spatialExtent.west,
            job.subsetDescriptor.spatialExtent.east,
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