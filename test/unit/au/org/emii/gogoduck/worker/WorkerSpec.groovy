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

    def "creates job directory"() {
        given:
        def worker = Spy(Worker)
        worker.outputPath = 'path'
        worker.outputFilename = 'file.nc'
        worker.job = new Job(uuid: '123')
        worker.metaClass.getCmd = { }
        worker.metaClass.execute = { }

        when:
        worker.run()

        then:
        1 * worker.mkJobDir('path/123')

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

        when:
        worker.run()

        then:
        executedCmd == 'thecommand'
    }

    def "generates full file path"() {
        given:
        def job = TestHelper.createJob()
        job.metaClass.getUuid = {
            'theId'
        }

        def worker = new Worker(
            shellCmd: { "gogoduck.sh ${it}" },
            job: job,
            outputPath: '/some/path',
            outputFilename: 'output.nc',
            fileLimit: 123
        )

        expect:
        worker.getFullOutputFilename() == '/some/path/theId/output.nc'
    }
}
