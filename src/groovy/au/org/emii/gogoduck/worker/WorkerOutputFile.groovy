package au.org.emii.gogoduck.worker

/*
 * Copyright 2014 IMOS
 *
 * The AODN/IMOS Portal is distributed under the terms of the GNU General Public License
 *
 */

class WorkerOutputFile {

    static def outputFilename(outputFilename) {
        String.format("%s%s.nc", outputFilename, new Date().format("dd-MM-YYYY-HH:mm:ss"))
    }

    static def aggReportOutputFilename(outputFilename) {
        String.format("%sreport.txt", outputFilename, new Date().format("dd-MM-YYYY-HH:mm:ss"))
    }
}
