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
            outputFilename: 'output.nc',
            fileLimit: 123
        )
        worker.metaClass.getFullOutputFilename = {
            "output.nc"
        }

        expect:
        worker.getCmd() == "gogoduck.sh -p some_layer -s TIME,2013-11-20T00:30:00.000Z,2013-11-20T10:30:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219 -o output.nc -l 123"
    }

    def "generates command line from job with no whitespace"() {
        given:
        def worker = new Worker(
            shellCmd: { "gogoduck.sh ${it}" },
            job: testJob,
            outputFilename: 'output.nc',
            fileLimit: 123
        )
        worker.metaClass.getFullOutputFilename = {
            "output.nc"
        }

        worker.job.subsetDescriptor.spatialExtent = new SpatialExtent(
            north:  90,
            south: -90,
            east:  180,
            west: -180
        )

        expect:
        worker.getCmd() == "gogoduck.sh -p some_layer -s TIME,2013-11-20T00:30:00.000Z,2013-11-20T10:30:00.000Z;LATITUDE,-90.0,90.0;LONGITUDE,-180.0,180.0 -o output.nc -l 123"
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
            Job job, String anErrMsg ->

            assertEquals job, testJob
            assertEquals expectErrMsg, anErrMsg
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
        exitValue | executeException              |  expectErrMsg             | expectSuccessCalled
        0         | null                          |  null                     | true
        1         | null                          | 'some error'              | false
        0         | new IOException('cannot run') | 'cannot run'              | false
    }
}
