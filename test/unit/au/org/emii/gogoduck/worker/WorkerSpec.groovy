package au.org.emii.gogoduck.worker

import au.org.emii.gogoduck.job.SpatialExtent
import org.apache.commons.io.IOUtils
import spock.lang.Specification
import spock.lang.Unroll

import au.org.emii.gogoduck.job.Job
import au.org.emii.gogoduck.test.TestHelper

class WorkerSpec extends Specification {

    def testJob
    def worker

    def setup() {
        testJob = TestHelper.createJob()
        worker = new Worker(job: testJob)
    }

    def "generates command line from job"() {
        given:
        def worker = new Worker(
            shellCmd: { "gogoduck.sh ${it}" },
            job: testJob,
            outputFilename: "output.nc",
            reportFilename: "report.txt",
            fileLimit: 123
        )

        expect:
        worker.getCmd() == "gogoduck.sh -p some_layer -s TIME,2013-11-20T00:30:00.000Z,2013-11-20T10:30:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219 -g geoserver_address -o output.nc -u report.txt -l 123"
    }

    def "generates command line from job when geoserver is null"() {
        given:
        testJob.geoserver = null

        def worker = new Worker(
            shellCmd: { "gogoduck.sh ${it}" },
            job: testJob,
            outputFilename: "output.nc",
            reportFilename: "report.txt",
            fileLimit: 123
        )

        expect:
        worker.getCmd() == "gogoduck.sh -p some_layer -s TIME,2013-11-20T00:30:00.000Z,2013-11-20T10:30:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219 -o output.nc -u report.txt -l 123"
    }

    def "generates command line from job when geoserver is empty string"() {
        given:
        testJob.geoserver = ""

        def worker = new Worker(
            shellCmd: { "gogoduck.sh ${it}" },
            job: testJob,
            outputFilename: "output.nc",
            reportFilename: "report.txt",
            fileLimit: 123
        )

        expect:
        worker.getCmd() == "gogoduck.sh -p some_layer -s TIME,2013-11-20T00:30:00.000Z,2013-11-20T10:30:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219 -o output.nc -u report.txt -l 123"
    }

    def "runs command"() {
        given:
        worker.job = [ uuid: '123' ]
        worker.metaClass.getCmd = {
            'thecommand'
        }
        def executedCmd
        worker.metaClass.execute = {
            executedCmd = it
            [
                exitValue: { 0 },
                getInputStream: {
                    IOUtils.toInputStream('some output', 'UTF-8')
                }
            ]
        }
        worker.metaClass.mkJobDir = {}
        worker.metaClass.writeJobToJsonFile = {}

        when:
        worker.run({}, {})

        then:
        executedCmd == 'thecommand'
    }

    @Unroll
    def "calls appropriate handler"() {
        given:
        def successCalled = false
        def successHandler = {
            Job job ->

                assertEquals job, testJob
                successCalled = true
        }

        def failureCalled = false
        def failureHandler = {
            Job job ->

                assertEquals job, testJob
                failureCalled = true
        }

        worker.metaClass.execute = {
            cmd ->

                if (executeException) {
                    throw executeException
                }

                [
                    exitValue: { exitValue },
                    getInputStream: {
                        IOUtils.toInputStream('some output', 'UTF-8')
                    },
                    getErrorStream: {
                        IOUtils.toInputStream(expectErrMsg, 'UTF-8')
                    }
                ]
        }

        worker.metaClass.getCmd = { "the command" }

        when:
        worker.run(successHandler, failureHandler)

        then:
        successCalled == expectSuccessCalled
        failureCalled != expectSuccessCalled

        where:
        exitValue | executeException              | expectSuccessCalled
        0         | null                          | true
        1         | null                          | false
        0         | new IOException('cannot run') | false
    }
}
