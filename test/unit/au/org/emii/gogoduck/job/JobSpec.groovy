package au.org.emii.gogoduck.job

import grails.test.mixin.*
import org.joda.time.*
import spock.lang.Specification

import au.org.emii.gogoduck.test.TestHelper

// Hack to load converters:
// http://grails.1312388.n4.nabble.com/Error-using-as-JSON-converter-in-a-service-in-a-unit-test-tp4637433p4637457.html
@TestFor(JobController)
class JobSpec extends Specification {

    def jobAsJson = '''{
   "subsetDescriptor": {
      "spatialExtent": {
         "south": "-33.433849",
         "north": "-32.150743",
         "east": "115.741219",
         "west": "114.15197"
      },
      "temporalExtent": {
         "start": "2013-11-20T00:30:00.000Z",
         "end": "2013-11-20T10:30:00.000Z"
      }
   },
   "createdTimestamp": "1970-01-01T11:00:01.234+11:00",
   "emailAddress": "gogo@duck.com",
   "layerName": "some_layer",
   "uuid": "1234"
}'''

    def setup() {
        DateTimeUtils.setCurrentMillisFixed(1234)
    }

    def cleanup() {
        DateTimeUtils.setCurrentMillisSystem()
    }

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

    def "initialised with a timestamp"() {
        given:
        DateTimeUtils.setCurrentMillisFixed(1234)
        def job = new Job()

        expect:
        job.createdTimestamp == DateTime.now()
    }

    def "to JSON"() {
        given:
        def job = TestHelper.createJob()
        job.uuid = '1234'


        expect:
        job.toJsonString() == jobAsJson
    }

    def "from JSON"() {
        given:
        def job = Job.fromJsonString(jobAsJson)

        expect:
        job.uuid == '1234'
        job.createdTimestamp == DateTime.now()
        job.emailAddress == "gogo@duck.com"
        job.layerName == "some_layer"
        job.subsetDescriptor.spatialExtent.south == "-33.433849"
        job.subsetDescriptor.temporalExtent.start == "2013-11-20T00:30:00.000Z"
    }

    def "aggr URL"() {
        given:
        def job = TestHelper.createJob()
        def jobUuid = '1234'
        job.uuid = jobUuid

        expect:
        job.getAggrUrl().toString() == "${job.serverURL}/aggr/${jobUuid}"
    }
}
