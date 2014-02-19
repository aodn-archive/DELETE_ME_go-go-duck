package au.org.emii.gogoduck.job

class JobExecutorService {

    def run(job) {
        execute('./gogoduck.sh')

    }

    def execute(cmd) {
        cmd.execute()
    }
}