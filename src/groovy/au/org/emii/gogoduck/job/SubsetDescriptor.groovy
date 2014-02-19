package au.org.emii.gogoduck.job

@grails.validation.Validateable
class SubsetDescriptor {
    SpatialExtent spatialExtent
    TemporalExtent temporalExtent

    static constraints = {
        spatialExtent nullable: false
        temporalExtent nullable: false
    }
}
