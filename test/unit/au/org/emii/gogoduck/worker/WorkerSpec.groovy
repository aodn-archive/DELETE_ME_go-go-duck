package au.org.emii.gogoduck.worker

import grails.test.mixin.*
import org.apache.commons.io.IOUtils
import spock.lang.Specification

import au.org.emii.gogoduck.job.Job
import au.org.emii.gogoduck.test.TestHelper

class WorkerSpec extends Specification {

    def job
    def worker

    def setup() {
        job = TestHelper.createJob()
        worker = new Worker(job: job)
    }

    def "generates command line from job"() {
        given:
        def worker = new Worker(
            shellCmd: { "gogoduck.sh ${it}" },
            job: job,
            outputFilename: 'output.nc',
            fileLimit: 123
        )
        worker.metaClass.getFullOutputFilename = {
            "output.nc"
        }

        expect:
        worker.getCmd() == "gogoduck.sh -p some_layer -s \"TIME,2013-11-20T00:30:00.000Z,2013-11-20T10:30:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219\" -o output.nc -l 123"
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
            [ exitValue: { 0 } ]
        }
        worker.metaClass.mkJobDir = { }
        worker.metaClass.writeJobToJsonFile = { }

        when:
        worker.run({}, {})

        then:
        executedCmd == 'thecommand'
    }

    // TODO: can we parameterise the following two methods?
    def "calls 'successHandler' on success"() {
        given:
        def successCalled = false
        def successHandler = {
            Job ajob ->
            successCalled = (ajob == job)
        }
        def failureCalled = false
        def failureHandler = {
            Job ajob, String errMs ->
            failureCalled = (ajob == job)
        }

        worker.metaClass.execute = {
            cmd ->

                [ exitValue: { 0 } ]
        }

        worker.metaClass.getCmd = { "the command" }

        when:
        worker.run(successHandler, failureHandler)

        then:
        successCalled
        !failureCalled
    }

    def "calls 'failureHandler' on failure"() {
        given:
        def successCalled = false
        def successHandler = {
            Job ajob ->
            successCalled = (ajob == job)
        }
        def errMsg = 'some error message'

        def failureCalled = false
        def failureHandler = {
            Job ajob, String anErrMsg ->
            failureCalled = (ajob == job && anErrMsg == errMsg)
        }

        worker.metaClass.execute = {
            cmd ->

                [
                    exitValue: { 1 },
                    getErrorStream: {
                        IOUtils.toInputStream(errMsg, 'UTF-8')
                    }
                ]
        }

        worker.metaClass.getCmd = { "the command" }

        when:
        worker.run(successHandler, failureHandler)

        then:
        !successCalled
        failureCalled
    }
}
