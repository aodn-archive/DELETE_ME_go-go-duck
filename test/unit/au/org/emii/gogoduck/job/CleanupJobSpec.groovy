package au.org.emii.gogoduck.job

import grails.test.mixin.*
import org.joda.time.*
import spock.lang.Specification

class CleanupJobSpec extends Specification {
    def setup() {
        DateTimeUtils.setCurrentMillisFixed(11111111)
    }

    def cleanup() {
        DateTimeUtils.setCurrentMillisSystem()
    }

    def "removes job directories older than 'n' days"() {
        given:
        def job1 = [ uuid: '1', createdTimestamp: DateTime.now().minusDays(10) ]
        def job2 = [ uuid: '2', createdTimestamp: DateTime.now().minusDays(11) ]
        def job3 = [ uuid: '3', createdTimestamp: DateTime.now().minusDays(9)  ]
        def job4 = [ uuid: '4', createdTimestamp: DateTime.now().minusDays(12) ]

        def jobStoreService = Mock(JobStoreService)
        def cleanupJob = new CleanupJob()
        cleanupJob.jobStoreService = jobStoreService
        cleanupJob.grailsApplication = [ config: [ job: [ cleanup: [ daysToKeep: 10 ] ] ] ]

        when:
        cleanupJob.execute()

        then:
        1 * jobStoreService.list() >> [ job1, job2, job3, job4 ]
        1 * jobStoreService.delete([job2, job4])
    }
}
