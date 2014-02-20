package au.org.emii.gogoduck.worker

import au.org.emii.gogoduck.job.Job

class Worker {
    Job job
    Closure shellCmd
    String outputPath
    String outputFilename
    Integer fileLimit

    void run() {
        mkJobDir(getPath())
        execute(getCmd())
    }

    def mkJobDir(path) {
        log.debug("Making directory: ${path}")
        new File(path).mkdirs()
    }

    def getPath() {
        "${outputPath}${File.separator}${job.getUuid()}"
    }

    def getFullOutputFilename() {
        "${getPath()}${File.separator}${outputFilename}"
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
            fullOutputFilename,
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