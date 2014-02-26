package au.org.emii.gogoduck.worker

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.job.Job
import au.org.emii.gogoduck.test.TestHelper

class WorkerSpec extends Specification {

    def "generates command line from job"() {
        given:
        def job = TestHelper.createJob()
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
        def worker = new Worker()
        worker.job = [ uuid: '123' ]
        worker.metaClass.getCmd = {
            'thecommand'
        }
        def executedCmd
        worker.metaClass.execute = {
            executedCmd = it
        }
        worker.metaClass.mkJobDir = { }
        worker.metaClass.writeJobToJsonFile = { }

        when:
        worker.run({}, {})

        then:
        executedCmd == 'thecommand'
    }
}
