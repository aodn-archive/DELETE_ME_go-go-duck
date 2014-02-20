package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper
import au.org.emii.gogoduck.worker.Worker

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {

    def "runs worker"() {
        given:
        def job = TestHelper.createJob()
        def worker = Mock(Worker)

        service.metaClass.getWorker = {
            worker
        }

        when:
        service.run(job)

        then:
        1 * worker.run()
    }
}
