package au.org.emii.gogoduck.job

@grails.validation.Validateable
class SpatialExtent {
    String north
    String south
    String east
    String west

    static constraints = {
    }
}
