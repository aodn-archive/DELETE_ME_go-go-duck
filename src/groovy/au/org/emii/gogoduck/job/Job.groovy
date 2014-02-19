package au.org.emii.gogoduck.job

@grails.validation.Validateable
class Job {
    String emailAddress
    String layerName
    SubsetDescriptor subsetDescriptor

    static constraints = {
        emailAddress email: true
        layerName blank: false
        subsetDescriptor nullable: false
    }
}
