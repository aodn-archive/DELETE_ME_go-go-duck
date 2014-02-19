package au.org.emii.gogoduck.job

class JobExecutorService {

    def grailsApplication

    def run(job) {
        job.outputFilename = grailsApplication.config.worker.outputFilename
        execute(grailsApplication.config.worker.cmd.call(job.toCmdString()))
    }

    def execute(cmd) {
        log.info("Executing command: '${cmd}'")

        def sout = new StringBuffer()
        def serr = new StringBuffer()
        def proc = cmd.execute()

        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill(10000)
        log.info(sout)
        log.error(serr)
    }
}