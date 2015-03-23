package au.org.emii.gogoduck.job

import grails.test.mixin.*
import spock.lang.Specification
import spock.lang.Unroll

class JobQueueSpec extends Specification {

    @Unroll
    def "returns correct position in queue"() {
        given:
        def queue = new JobQueue()

        when:
        (0..2).each {
            queue.add([ job: [ uuid: it ] ])
        }

        then:
        expectedPosition == queue.getQueuePosition([ uuid: uuid ])

        where:
        uuid | expectedPosition
        0    | 0
        2    | 2
        123  | -1
    }
}
