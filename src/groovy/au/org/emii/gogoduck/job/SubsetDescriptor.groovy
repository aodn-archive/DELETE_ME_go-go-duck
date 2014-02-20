package au.org.emii.gogoduck.job

@grails.validation.Validateable
class SubsetDescriptor {
    SpatialExtent spatialExtent = new SpatialExtent()
    TemporalExtent temporalExtent = new TemporalExtent()

    static constraints = {
        spatialExtent nullable: false
        temporalExtent nullable: false
    }
}
