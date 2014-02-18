package au.org.emii.gogoduck.job

@grails.validation.Validateable
class Job {
    String emailAddress
    SpatialExtent spatialExtent
    TemporalExtent temporalExtent

    static constraints = {
        emailAddress email: true
        spatialExtent nullable: false
        temporalExtent nullable: false
    }
}
