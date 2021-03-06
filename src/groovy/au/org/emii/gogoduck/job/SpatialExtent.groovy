package au.org.emii.gogoduck.job

@grails.validation.Validateable
class SpatialExtent {
    Double north
    Double south
    Double east
    Double west

    static constraints = {
        north latitudeConstraints
        south latitudeConstraints
        east  longitudeConstraints
        west  longitudeConstraints
    }

    static def longitudeConstraints = [nullable: false, min: -180 as Double, max: 180 as Double]
    static def latitudeConstraints  = [nullable: false, min:  -90 as Double, max:  90 as Double]
}
