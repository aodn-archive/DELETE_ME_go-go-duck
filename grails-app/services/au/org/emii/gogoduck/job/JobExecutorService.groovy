package au.org.emii.gogoduck.job

class JobExecutorService {

    def grailsApplication

    def run(job) {
        job.outputFilename = grailsApplication.config.worker.outputFilename
        job.fileLimit = grailsApplication.config.worker.fileLimit

        execute(grailsApplication.config.worker.cmd.call(job.toCmdString()))
    }

    def execute(cmd) {
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