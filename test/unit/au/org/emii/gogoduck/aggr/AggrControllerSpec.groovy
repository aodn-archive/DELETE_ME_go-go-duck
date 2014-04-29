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
        job.createdTimestamp = "2014-04-29T14:44:07.913+10:00"
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
        bytes == tempFile.bytes
    }

    def "filename contains date"() {
        given:

        grailsApplication.config.worker.outputFilenamePrefixForUser = "user-prefix-"

        def jobStoreService = Mock(JobStoreService)
        controller.jobStoreService = jobStoreService

        def job = TestHelper.createJob()
        job.createdTimestamp = "2014-04-29T14:44:07.913+10:00"

        expect:
        controller.filenameToServe(job) == "user-prefix-20140429T144407.913+1000.nc"
    }
}
