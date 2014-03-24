package au.org.emii.gogoduck.job

import org.joda.time.format.DateTimeFormat

class DateFormatValidator {

    static def getFormatter() {

        DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    }

    static def isValid = {

        try {
            formatter.parseDateTime(it)

            return true
        }
        catch (Exception e) {}

        return false
    }
}
