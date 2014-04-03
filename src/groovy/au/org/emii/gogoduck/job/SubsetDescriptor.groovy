package au.org.emii.gogoduck.job

@grails.validation.Validateable
class SubsetDescriptor {
    SpatialExtent spatialExtent = new SpatialExtent()
    TemporalExtent temporalExtent = new TemporalExtent()

    static constraints = {
        spatialExtent nullable: false
        temporalExtent nullable: false
    }

    def getSubsetCommandString() {
        return String.format(
            "TIME,%s,%s;LATITUDE,%s,%s;LONGITUDE,%s,%s",
            temporalExtent.start,
            temporalExtent.end,
            spatialExtent.south,
            spatialExtent.north,
            spatialExtent.west,
            spatialExtent.east
        )
    }
}
