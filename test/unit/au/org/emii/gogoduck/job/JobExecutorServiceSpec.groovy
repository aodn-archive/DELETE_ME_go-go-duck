package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper
import au.org.emii.gogoduck.worker.Worker

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {

    def job
    def jobStoreService
    def notificationService
    def worker

    def setup() {
        job = TestHelper.createJob()
        job.uuid = '1234'
        jobStoreService = Mock(JobStoreService)
        service.jobStoreService = jobStoreService
        notificationService = Mock(NotificationService)
        service.notificationService = notificationService

        worker = Mock(Worker)

        service.metaClass.getWorker = {
            worker
        }
    }

    def "run sends 'job registered' notification"() {
        when:
        service.run(job)

        then:
        1 * notificationService.sendJobRegisteredNotification(job)
    }

    def "makes job dir, writes job as json to file"() {
        when:
        service.run(job)

        then:
        1 * jobStoreService.makeDir(job)
        1 * jobStoreService.writeToFileAsJson(job)
    }

    def "runs worker"() {
        when:
        service.run(job)

        then:
        1 * worker.run()
    }

    def "run sends 'job success' notification"() {
        when:
        service.run(job)

        then:
        1 * notificationService.sendJobSuccessNotification(job)
    }
}
