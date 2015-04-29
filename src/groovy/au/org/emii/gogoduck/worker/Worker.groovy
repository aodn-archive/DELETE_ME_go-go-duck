package au.org.emii.gogoduck.worker

import org.apache.commons.io.IOUtils

import au.org.emii.gogoduck.job.Job

class Worker {
    Job job
    Closure shellCmd
    String outputFilename
    String reportFilename
    Integer fileLimit
    Integer maxGogoduckTimeMinutes

    void run(successHandler, failureHandler) {
        try {
            def process = execute(getCmd())
            log.info("worker output: ${IOUtils.toString(process.getInputStream(), 'UTF-8')}")

            if (process.exitValue() == 0) {
                successHandler(job)
            }
            else {
                failureHandler(job)
            }
        }
        catch (IOException e) {
            failureHandler(job)
        }
    }

    def getCmd() {
        log.info "Temporary directory for gogoduck operation '${getTempDir()}'"

        def cmdOptions = String.format(
            "${job.subsetCommandString} -t %s -o %s -u %s -l %s",
            getTempDir(),
            outputFilename,
            reportFilename,
            fileLimit
        )

        log.info("Command options: '${cmdOptions}'")

        shellCmd.call(cmdOptions)
    }

    String getTempDir() {
        return System.getProperty("java.io.tmpdir")
    }

    Process execute(cmd) {
        log.info("Executing command: '${cmd}'")

        def proc = cmd.execute()
        proc.consumeProcessOutput(System.out, System.err)
        proc.waitForOrKill(maxGogoduckTimeMinutes * 60 * 1000) // Convert to ms

        return proc
    }
}
