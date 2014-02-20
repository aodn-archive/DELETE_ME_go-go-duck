package au.org.emii.gogoduck.worker

import au.org.emii.gogoduck.job.Job

class Worker {
    Job job
    Closure shellCmd
    String outputFilename
    Integer fileLimit

    void run() {
        execute(getCmd())
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

    void execute(cmd) {
        log.info("Executing command: '${cmd}'")

        def sout = new StringBuffer()
        def serr = new StringBuffer()

        def proc = cmd.execute()

        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill(10000)

        log.debug("Command output: ${sout}")
        log.debug("Command error: ${serr}")
    }
}