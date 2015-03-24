package au.org.emii.gogoduck.job

import grails.converters.JSON
import grails.test.mixin.*
import org.joda.time.*
import spock.lang.Specification
import spock.lang.Unroll

import au.org.emii.gogoduck.test.TestHelper

// Hack to load converters:
// http://grails.1312388.n4.nabble.com/Error-using-as-JSON-converter-in-a-service-in-a-unit-test-tp4637433p4637457.html
@TestFor(JobController)
class JobSpec extends Specification {

    def jobAsJson = '''{
   "subsetDescriptor": {
      "spatialExtent": {
         "south": -33.433849,
         "north": -32.150743,
         "east": 115.741219,
         "west": 114.15197
      },
      "temporalExtent": {
         "start": "2013-11-20T00:30:00.000Z",
         "end": "2013-11-20T10:30:00.000Z"
      }
   },
   "createdTimestamp": "1970-01-01T11:00:01.234+11:00",
   "status": "NEW",
   "geoserver": "geoserver_address",
   "emailAddress": "gogo@duck.com",
   "layerName": "some_layer",
   "uuid": "1234",
   "startedTimestamp": null,
   "finishedTimestamp": null
}'''

    def invalidJobValues = [
        emailAddress: "gogo@duck.com not an email address",
        layerName: "some_layer some extra text (or shell commands!)",
        subsetDescriptor: [
            spatialExtent: [
                "south": -91,
                "north": 95,
                "east": 200,
                "west": -800000
            ],
            temporalExtent: [
                "start": "2013-11-20T00:30:00.000Z some more text",
                "end": "2013-99-99T10:30:00.000Z"
            ]
        ]
    ]

    def setup() {
        DateTimeUtils.setCurrentMillisFixed(1234)

        JSON.registerObjectMarshaller(Enum) { Enum someEnum ->
            someEnum.toString()
        }
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
        job.subsetDescriptor.spatialExtent.south == -33.433849
        job.subsetDescriptor.temporalExtent.start == "2013-11-20T00:30:00.000Z"
    }

    def "validation protects against invalid values"() {
        given:
        def job = new Job(invalidJobValues)
        def subsetDescriptor = job.subsetDescriptor
        def temporalExtent = subsetDescriptor.temporalExtent
        def spatialExtent = subsetDescriptor.spatialExtent

        job.validate()
        subsetDescriptor.validate()
        temporalExtent.validate()
        spatialExtent.validate()

        expect:
        _getProblemFieldNames(job) == ['emailAddress', 'layerName']
        _getProblemFieldNames(temporalExtent) == ['start', 'end']
        _getProblemFieldNames(spatialExtent) == ['north', 'south', 'east', 'west']
    }

    @Unroll
    def "status change sets corresponding timestamp"() {
        given:
        def job = TestHelper.createJob()

        when:
        job.status = status

        then:
        job.status == status
        job[affectedTimestamp] == DateTime.now()

        where:
        status             | affectedTimestamp
        Status.NEW         | 'createdTimestamp'
        Status.IN_PROGRESS | 'startedTimestamp'
        Status.SUCCEEDED   | 'finishedTimestamp'
        Status.FAILED      | 'finishedTimestamp'
    }


    def _getProblemFieldNames(job) {

        def names = []

        job.errors.each {
            names.addAll it.fieldErrors.collect { it.field }.unique()
        }

        return names
    }
}
