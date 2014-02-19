package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {
    def "run shells out to gogoduck script"() {

        given:
        def job = TestHelper.createJob()
        def workerCmd = 'gogoduck.sh'
        service.grailsApplication = [ config: [ worker: [ cmd: workerCmd ] ] ]

        def cmd
        service.metaClass.execute = {
            cmd = it
        }

        when:
        service.run(job)

        then:

        cmd == "${workerCmd} -p some_layer -s \"TIME,2013-11-20T00:30:00.000Z,2013-11-20T10:30:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219\" -o output.nc"
    }
}
