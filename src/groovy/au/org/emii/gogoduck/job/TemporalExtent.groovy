package au.org.emii.gogoduck.job

@grails.validation.Validateable
class TemporalExtent {
    String start
    String end

    static constraints = {
        start blank: false
        end blank: false
    }
}
