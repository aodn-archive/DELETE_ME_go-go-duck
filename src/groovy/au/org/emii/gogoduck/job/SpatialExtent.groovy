package au.org.emii.gogoduck.job

@grails.validation.Validateable
class SpatialExtent {
    String north
    String south
    String east
    String west

    static constraints = {
        north nullable: false
        south nullable: false
        east  nullable: false
        west  nullable: false
    }

    String toCmdString() {
        "LATITUDE,${south},${north};LONGITUDE,${west},${east}"
    }
}
