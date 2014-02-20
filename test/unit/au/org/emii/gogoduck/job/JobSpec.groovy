package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper

class JobSpec extends Specification {
    def "initialised with a UUID"() {
        given:
        Set<String> existingIds = new HashSet<String>()

        expect:

        // Ok, not mathematically fool proof, but good enough for us (probably).
        1000.times {
            def job = TestHelper.createJob()
            assert job.getUuid()
            assert existingIds.add(job.getUuid())
        }
    }
}