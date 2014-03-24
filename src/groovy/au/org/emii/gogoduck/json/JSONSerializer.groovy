package au.org.emii.gogoduck.json

import grails.web.*
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

// Thanks to: http://stackoverflow.com/a/5543915/627806
class JSONSerializer {

    def target

    String getJSON() {

        Closure jsonFormat = {
            buildJSON.delegate = delegate
            buildJSON(target)
        }

        def json = new JSONBuilder().build(jsonFormat)
        return json.toString(true)
    }

    private buildJSON = {obj ->

        obj.properties.each {propName, propValue ->

            if (shouldSerialiseProperty(propName)) {

                serialiseProperty.delegate = delegate
                serialiseProperty(propName, propValue)
            }
        }
    }

    private def serialiseProperty = { name, value ->

        def valueToSet = value

        if (value instanceof DateTime) {
            valueToSet = ISODateTimeFormat.dateTime().print(value)
        }
        else if (isComplex(value)) {
            // create a nested JSON object and recursively call this function to serialize it
            valueToSet = {
                buildJSON(value)
            }
        }

        // It seems "name = value" doesn't work when name is dynamic so we need to
        // set the property on the builder using this syntax instead
        setProperty(name, valueToSet)
    }

   /**
     * A simple object is one that can be set directly as the value of a JSON property, examples include strings,
     * numbers, booleans, etc.
     *
     * @param propValue
     * @return
     */
    private boolean isSimple(propValue) {
        // This is a bit simplistic as an object might very well be Serializable but have properties that we want
        // to render in JSON as a nested object. If we run into this issue, replace the test below with an test
        // for whether value is an instanceof Number, String, Boolean, Char, etc.
        propValue instanceof Serializable || propValue == null
    }

    private boolean isComplex(propValue) {

        !isSimple(propValue)
    }

    def shouldSerialiseProperty(property) {

        def propertiesToOmit = [
            'class',
            'metaClass',
            'errors',
            'constraints',
            'grailsApplication',
            'aggrUrl',
            'serverUrl',
            'longitudeConstraints',
            'latitudeConstraints'
        ]

        return !propertiesToOmit.contains(property)
    }
}
