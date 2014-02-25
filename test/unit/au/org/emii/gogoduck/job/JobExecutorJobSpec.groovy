package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper
import au.org.emii.gogoduck.worker.Worker

class JobExecutorJobSpec extends Specification {
    def job
    def jobExecutorJob
    def jobExecutorService
    def jobStoreService
    def notificationService
    def worker

    def setup() {
        job = TestHelper.createJob()
        jobExecutorJob = new JobExecutorJob()

        jobStoreService = Mock(JobStoreService)
        jobExecutorJob.jobStoreService = jobStoreService
        notificationService = Mock(NotificationService)
        jobExecutorJob.notificationService = notificationService

        worker = Mock(Worker)

        jobExecutorJob.metaClass.getWorker = {
            worker
        }
    }

    def "makes job dir, writes job as json to file"() {
        when:
        jobExecutorJob.run(job)

        then:
        1 * jobStoreService.makeDir(job)
        1 * jobStoreService.writeToFileAsJson(job)
    }

    def "runs worker"() {
        when:
        jobExecutorJob.run(job)

        then:
        1 * worker.run()
    }

    def "run sends 'job success' notification"() {
        when:
        jobExecutorJob.run(job)

        then:
        1 * notificationService.sendJobSuccessNotification(job)
    }
}
