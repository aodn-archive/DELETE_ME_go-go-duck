package au.org.emii.gogoduck.job

import grails.converters.JSON
import org.joda.time.DateTime

import au.org.emii.gogoduck.json.JSONSerializer

@grails.validation.Validateable
class Job {
    String uuid
    String emailAddress
    String layerName
    String geoserver

    DateTime createdTimestamp
    DateTime startedTimestamp
    DateTime finishedTimestamp
    Status status

    // Need to instantiate nested objects, otherwise they are not bound.
    // See: http://grails.1312388.n4.nabble.com/How-to-bind-data-to-a-command-object-that-has-an-non-domain-object-as-property-tp4021559p4328826.html
    SubsetDescriptor subsetDescriptor = new SubsetDescriptor()

    static constraints = {
        emailAddress email: true
        layerName blank: false, validator: { it ==~ /^[\w]+$/ }
        geoserver nullable: true
        subsetDescriptor nullable: false
    }

    Job() {
        uuid = UUID.randomUUID().toString()[0..7]
        createdTimestamp = DateTime.now()
        status = Status.NEW
    }

    String toString() {
        toJsonString()
    }

    URL getAggrUrl() {
        new URL("$serverUrl/aggr/$uuid")
    }

    void setStatus(status) {
        this.status = status

        switch (status) {
            case Status.IN_PROGRESS:
                this.startedTimestamp = DateTime.now()
                break;

            case Status.SUCCEEDED:
            case Status.FAILED:
                this.finishedTimestamp = DateTime.now()
                break;

            default:
            break;
        }
    }

    def getSubsetCommandString() {
        def subsetCommandString = ""

        subsetCommandString +=
            String.format(
                "-p %s -s ${subsetDescriptor.subsetCommandString}",
                layerName
            )

        if (geoserver) {
            subsetCommandString += String.format(" -g %s", geoserver)
        }

        return subsetCommandString
    }

    public String toJsonString() {
        // Groovy 2.0. whatever grails 2.2.0 uses has a bug:
        // http://stackoverflow.com/questions/14406981/why-do-i-get-a-stackoverflowerror-on-when-groovy-jsonbuilder-tries-to-serialize
        new JSONSerializer(target: this).getJSON()
    }

    void setCreatedTimestamp(String tsAsString) {
        this.createdTimestamp = new DateTime(tsAsString)
    }

    void setStartedTimestamp(String tsAsString) {
        if (tsAsString && tsAsString != "null") {
            this.startedTimestamp = new DateTime(tsAsString)
        }
    }

    void setFinishedTimestamp(String tsAsString) {
        if (tsAsString && tsAsString != "null") {
            this.finishedTimestamp = new DateTime(tsAsString)
        }
    }

    static Job fromJsonString(jobAsJson) {
        new Job(JSON.parse(jobAsJson))
    }
}
