package au.org.emii.gogoduck.job

import org.joda.time.DateTime

class CleanupJob {
    def sessionRequired = false
    def concurrent = false

    def grailsApplication
    def jobStoreService

    def execute() {
        log.info("Cleaning up old jobs...")
        jobStoreService.delete(
            jobStoreService.list().grep {
                it.createdTimestamp.plusDays(grailsApplication.config.job.cleanup.daysToKeep).isBefore(DateTime.now())
            }
        )
    }
}
