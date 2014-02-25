package au.org.emii.gogoduck.job

class JobExecutorService {

    def jobExecutorJob
    def notificationService

    def run(job) {
        notificationService.sendJobRegisteredNotification(job)
        jobExecutorJob.run(job)
    }
}