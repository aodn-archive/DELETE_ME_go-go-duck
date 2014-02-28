package au.org.emii.gogoduck.aggr

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.job.*
import au.org.emii.gogoduck.test.TestHelper

@TestFor(AggrController)
class AggrControllerSpec extends Specification {

    def "show results in download"() {
        given:

        def jobStoreService = Mock(JobStoreService)
        controller.jobStoreService = jobStoreService

        File tempFile = File.createTempFile("tempFile-file-name", ".tmp")
        tempFile << "some bytes"

        def job = TestHelper.createJob()
        def jobId = 'asdf'
        job.uuid = jobId
        1 * jobStoreService.get(jobId) >> job
        1 * jobStoreService.getAggrFile(job) >> tempFile

        def bytes
        controller.response.outputStream.metaClass.write = {
            byte[] b ->

            bytes = b
        }

        when:
        controller.params.uuid = jobId

        controller.show()

        then:
        response.contentType == "application/octet-stream"
        response.header("Content-disposition") == "filename=${tempFile.name}"
        bytes == tempFile.bytes
    }
}
