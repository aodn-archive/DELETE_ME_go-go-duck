package au.org.emii.gogoduck.worker

/*
 * Copyright 2014 IMOS
 *
 * The AODN/IMOS Portal is distributed under the terms of the GNU General Public License
 *
 */

class WorkerOutputFile {

    static def outputFilename(outputFilename,jobHash, extension) {
        String.format("%s%s%s", outputFilename, jobHash, extension)
    }

    static def aggReportOutputFilename(outputFilename) {
        String.format("%sreport.txt", outputFilename)
    }
}
