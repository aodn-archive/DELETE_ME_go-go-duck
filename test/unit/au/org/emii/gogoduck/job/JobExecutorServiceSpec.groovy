package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper
import au.org.emii.gogoduck.worker.Worker

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {

    def job
    def jobStoreService
    def worker

    def setup() {
        job = TestHelper.createJob()
        jobStoreService = Mock(JobStoreService)
        service.jobStoreService = jobStoreService

        worker = Mock(Worker)

        service.metaClass.getWorker = {
            worker
        }
    }

    def "run sends 'job registered' notification email"() {
        given:
        def job = TestHelper.createJob()
        def mailService = Mock(MailService)
        service.mailService = mailService

        when:
        service.run(job)

        then:
        1 * mailService.sendMail()
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
}
