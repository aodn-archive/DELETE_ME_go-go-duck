package au.org.emii.gogoduck.worker

import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.joda.time.Duration

import au.org.emii.gogoduck.job.Job
import au.org.emii.gogoduck.job.Reason

class Worker {
    Job job
    Closure shellCmd
    String outputFilename
    String reportFilename
    Integer fileLimit
    Integer maxGogoduckTimeMinutes

    Integer TOO_MANY_FILES_EXIT_CODE = 3

    void run(successHandler, failureHandler) {
        def startTime = DateTime.now()
        def process

        try {
            process = execute(getCmd())
            log.info("worker output: ${IOUtils.toString(process.getInputStream(), 'UTF-8')}")

            if (process.exitValue() == 0) {
                successHandler(job)
                return
            }
        }
        catch (IOException e) {
            log.warn "Exception while processing request: ", e
            Reason reason = getReason(process, startTime, DateTime.now())
            failureHandler(job, reason)
            return
        }

        Reason reason = getReason(process, startTime, DateTime.now())
        failureHandler(job, reason)
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

    Boolean timeoutError(startTime, endTime, maxGogoduckTimeMinutes) {
        Duration jobDuration = new Duration(startTime, endTime)
        Duration maxDuration = new Duration(maxGogoduckTimeMinutes * 60 * 1000)
        return jobDuration >= maxDuration
    }

    Reason getReason(process, startTime, endTime) {
        if (process && process.exitValue() == TOO_MANY_FILES_EXIT_CODE) {
            return Reason.TOO_MANY_FILES
        }
        else if (timeoutError(startTime, endTime, maxGogoduckTimeMinutes)) {
            return Reason.TIMEOUT_EXPIRED
        }
        else {
            return Reason.GOGODUCK_CORE
        }
    }
}
