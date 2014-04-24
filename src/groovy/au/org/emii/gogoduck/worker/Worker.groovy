package au.org.emii.gogoduck.worker

import au.org.emii.gogoduck.job.JobExecutorJob
import au.org.emii.gogoduck.job.JobStoreService
import org.apache.commons.io.IOUtils

import au.org.emii.gogoduck.job.Job

class Worker {
    Job job
    Closure shellCmd
    String creationTime
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
                String errMsg = IOUtils.toString(process.getErrorStream(), 'UTF-8').trim()
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
        def cmdOptions = String.format(
            "${job.subsetCommandString} -o %s -u %s -l %s",
            outputFilename,
            WorkerOutputFile.aggReportOutputFilename(outputFilename),
            fileLimit
        )

        log.info("Command options: '${cmdOptions}'")

        shellCmd.call(cmdOptions)
    }

    Process execute(cmd) {
        log.info("Executing command: '${cmd}'")

        def proc = cmd.execute()
        proc.waitFor()

        return proc
    }
}
