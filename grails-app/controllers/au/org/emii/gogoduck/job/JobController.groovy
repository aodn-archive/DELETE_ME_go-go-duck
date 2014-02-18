/*
 * Copyright 2014 IMOS
 *
 * The AODN/IMOS Portal is distributed under the terms of the GNU General Public License
 *
 */
package au.org.emii.gogoduck.job

class JobController {

    static allowedMethods = [ save: "POST" ]

    def save(Job job) {

        if (job.hasErrors()) {
            render (status: 400, text: "Invalid request format: ${job.errors}")
        }
        else {
            render (status: 200)
        }
    }
}

@grails.validation.Validateable
class TemporalExtent {
    String start
    String end

    static constraints = {
        start blank: false
        end blank: false
    }
}

@grails.validation.Validateable
class SpatialExtent {
    String north
    String south
    String east
    String west

    static constraints = {
    }
}

@grails.validation.Validateable
class Job {
    String emailAddress
    SpatialExtent spatialExtent
    TemporalExtent temporalExtent

    static constraints = {
        emailAddress email: true
        spatialExtent nullable: false
        temporalExtent nullable: false
    }
}
