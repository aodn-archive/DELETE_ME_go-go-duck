package au.org.emii.gogoduck.job

@grails.validation.Validateable
class Job {
    String id
    String emailAddress
    String layerName

    // Need to instantiate nested objects, otherwise they are not bound.
    // See: http://grails.1312388.n4.nabble.com/How-to-bind-data-to-a-command-object-that-has-an-non-domain-object-as-property-tp4021559p4328826.html
    SubsetDescriptor subsetDescriptor = new SubsetDescriptor()

    static constraints = {
        emailAddress email: true
        layerName blank: false
        subsetDescriptor nullable: false
    }

    Job() {
        id = UUID.randomUUID().toString()[0..7]
    }
}
