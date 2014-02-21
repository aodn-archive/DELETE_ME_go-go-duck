package au.org.emii.gogoduck.job

import grails.converters.JSON
import au.org.emii.gogoduck.json.JSONSerializer

@grails.validation.Validateable
class Job {
    String uuid
    String emailAddress
    String layerName

    // Need to instantiate nested objects, otherwise they are not bound.
    // See: http://grails.1312388.n4.nabble.com/How-to-bind-data-to-a-command-object-that-has-an-non-domain-object-as-property-tp4021559p4328826.html
    SubsetDescriptor subsetDescriptor = new SubsetDescriptor()

    static constraints = {
        emailAddress email: true
        layername blank: false
        subsetDescriptor nullable: false
    }

    Job() {
        uuid = UUID.randomUUID().toString()[0..7]
    }

    String toString() {
        toJsonString()
    }

    public String toJsonString() {
        new JSONSerializer(target: this).getJSON()
    }

    static Job fromJsonString(jobAsJson) {
        new Job(JSON.parse(jobAsJson))
    }
}