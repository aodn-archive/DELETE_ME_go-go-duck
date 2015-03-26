package au.org.emii.gogoduck.job

import grails.converters.JSON
import grails.test.mixin.*
import spock.lang.Specification
import spock.lang.Unroll

import au.org.emii.gogoduck.test.TestHelper

@TestFor(JobController)
class JobControllerSpec extends Specification {

    def mockJob

    def setup() {
        controller.jobStoreService =  Mock(JobStoreService)
        mockJob = new Object()
        mockJob.metaClass.uuid = 123

        controller.jobStoreService.get(_) >> mockJob
        controller.jobExecutorService =  Mock(JobExecutorService)
    }

    def "save with valid request registers job"() {
        given:
        def job = TestHelper.createJob()
        JobExecutorService jobExecutorService = Mock()
        controller.jobExecutorService = jobExecutorService

        when:
        controller.save(job)

        then:
        response.status == 200
        1 * jobExecutorService.register(job)
    }

    @Unroll("show #status job")
    def "show job"() {
        given:
        controller.jobStoreService =  Mock(JobStoreService)
        mockJob = new Object()
        mockJob.metaClass.uuid = 123

        controller.jobStoreService.get(_) >> mockJob
        controller.jobExecutorService =  Mock(JobExecutorService)

        mockJob.metaClass.status = status
        controller.jobExecutorService.getQueuePosition(_) >> queuePosition
        controller.jobStoreService.getReport(_) >> report

        def aggrUrl = hasAggrUrl ?
            controller.createLink(controller: 'aggr', action: 'show', id: mockJob.uuid, absolute: true) : null

        when:
        controller.params.id = 123
        def model = controller.show()

        then:
        queuePosition ? queuePosition + 1 : null == model.job.queuePosition
        aggrUrl == model.job.aggrUrl
        report == model.job.report

        where:
        status             | queuePosition | report        | hasAggrUrl
        Status.NEW         | 5             | null          | null
        Status.IN_PROGRESS | null          | null          | null
        Status.SUCCEEDED   | null          | 'report text' | true
        Status.FAILED      | null          | 'report text' | false
    }
}
