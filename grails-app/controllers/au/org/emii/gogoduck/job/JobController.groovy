/*
 * Copyright 2014 IMOS
 *
 * The AODN/IMOS Portal is distributed under the terms of the GNU General Public License
 *
 */
package au.org.emii.gogoduck.job

class JobController {

    static allowedMethods = [ save: "POST" ]

    def save = {

        if (!params.emailAddress) {
            response.status = 400
            render text: "Invalid request format: 'emailAddress' must be specified."
        }
        else if (!params.temporalExtent) {
            response.status = 400
            render text: "Invalid request format: 'temporalExtent' must be specified."
        }
        else if (!params.spatialExtent) {
            response.status = 400
            render text: "Invalid request format: 'spatialExtent' must be specified."
        }
        else {
            response.status = 200
        }
    }
}
