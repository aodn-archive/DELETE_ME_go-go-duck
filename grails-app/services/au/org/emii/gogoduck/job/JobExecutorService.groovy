package au.org.emii.gogoduck.job

class JobExecutorService {

    def grailsApplication

    def run(job) {
        execute("${grailsApplication.config.worker.cmd} ${job.toCmdString()}")
    }

    def execute(cmd) {
        log.info("Executing command: '${cmd}'")
        cmd.execute()
    }
}