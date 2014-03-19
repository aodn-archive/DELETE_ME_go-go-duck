package au.org.emii.gogoduck.job

import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

@grails.validation.Validateable
class TemporalExtent {
    String start
    String end

    static constraints = {
        start validator: dateFormatValidator
        end   validator: dateFormatValidator
    }

    static def dateFormatValidator = {

        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            fmt.parseDateTime(it)

            return true
        }
        catch (Exception e) {

            return false
        }
    }
}
