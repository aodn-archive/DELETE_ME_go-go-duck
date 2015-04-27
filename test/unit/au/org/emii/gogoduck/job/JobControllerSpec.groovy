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
        controller.jobStoreService = Mock(JobStoreService)
        mockJob = new Object()
        mockJob.metaClass.uuid = 123

        controller.jobStoreService.get(_) >> mockJob
        controller.jobExecutorService = Mock(JobExecutorService)
    }

    def "save with valid request registers job"() {
        given:
        def job = TestHelper.createJob()
        controller.jobExecutorService.getQueuePosition(_) >> 1

        def expectedUrl = controller.createLink(controller: 'job', action: 'show', id: job.uuid, absolute: true)

        when:
        controller.save(job)
        def responseJson = JSON.parse(response.text)

        then:
        response.status == 200
        responseJson.url == expectedUrl
        1 * controller.jobExecutorService.register(job)
    }

    @Unroll("show #status job")
    def "show job"() {
        given:
        mockJob.metaClass.status = status
        controller.jobExecutorService.getQueuePosition(_) >> queuePosition
        controller.jobStoreService.getReport(_) >> report

        def presentedQueuePosition = queuePosition ? (queuePosition + 1) : null

        def aggrUrl = hasAggrUrl ?
            controller.createLink(controller: 'aggr', action: 'show', id: mockJob.uuid, absolute: true) : null

        when:
        controller.params.id = 123
        def model = controller.show()

        then:
        presentedQueuePosition == model.job.queuePosition
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
