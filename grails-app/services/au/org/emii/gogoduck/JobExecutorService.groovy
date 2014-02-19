package au.org.emii.gogoduck.job

class JobExecutorService {

    def grailsApplication

    def run(job) {
        execute("${grailsApplication.worker.cmd} ${job.toCmdString()}")
    }

    def execute(cmd) {
        cmd.execute()
    }
}