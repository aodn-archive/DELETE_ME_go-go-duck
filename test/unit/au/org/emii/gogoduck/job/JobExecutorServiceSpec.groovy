package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

@TestFor(JobExecutorService)
class JobExecutorServiceSpec extends Specification {
    def "run shells out to gogoduck script"() {

        given:
        def job = TestHelper.createJob()
        def cmd
        service.metaClass.execute = {
            cmd = it
        }

        when:
        service.run(job)

        then:
        cmd == './gogoduck.sh'
    }
}
