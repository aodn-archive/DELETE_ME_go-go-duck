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

    String getCmd() {
        // TODO: remove these from job
        job.fileLimit = fileLimit
        job.outputFilename = outputFilename

        shellCmd.call(job.toCmdString())
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