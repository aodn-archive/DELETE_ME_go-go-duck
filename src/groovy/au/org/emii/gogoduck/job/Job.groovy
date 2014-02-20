package au.org.emii.gogoduck.job

@grails.validation.Validateable
class Job {
    String emailAddress
    String layerName
    String outputFilename
    Integer fileLimit

    // Need to instantiate nested objects, otherwise they are not bound.
    // See: http://grails.1312388.n4.nabble.com/How-to-bind-data-to-a-command-object-that-has-an-non-domain-object-as-property-tp4021559p4328826.html
    SubsetDescriptor subsetDescriptor = new SubsetDescriptor()

    static constraints = {
        emailAddress email: true
        layerName blank: false
        subsetDescriptor nullable: false
    }

    String toCmdString() {
        "-p ${layerName} ${subsetDescriptor.toCmdString()} -o ${outputFilename} -l ${fileLimit}"
    }
}
