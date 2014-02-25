package au.org.emii.gogoduck.job

class JobExecutorService {

    def notificationService

    def run(job) {
        notificationService.sendJobRegisteredNotification(job)
        JobExecutorJob.triggerNow([job: job])
    }
}