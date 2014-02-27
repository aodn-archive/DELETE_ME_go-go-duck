package au.org.emii.gogoduck

import groovy.io.FileType

class JobCleanupController {

    def index() {

        def path = grailsApplication.config.worker.outputPath
        log.debug("Looking in folder  " + path)
        def diskSpaceUsed = 0
        def filesToCheck = []

        new File(path).eachFileRecurse (FileType.FILES) { file ->
            filesToCheck << file
            diskSpaceUsed += file.size()
        }
        diskSpaceUsed = _prettySize(diskSpaceUsed)
        def diskSpaceGained = _deleteOldFiles(filesToCheck)

        render ("DiskSpaceUsed by ${path}:  ${diskSpaceUsed}<br>DiskSpaceGained:  ${diskSpaceGained}")

    }

    def _deleteOldFiles(files) {

        def cutOffDate = new Date() - grailsApplication.config.worker.fileAgeDays
        log.debug("Looking for files older than " + cutOffDate)
        def diskSpaceGained = 0

        files.each {
            def thisDate = new Date(it.lastModified())
            def thisSize = it.size()
            if (thisDate.before(cutOffDate)) {
                if (new File(it.path).delete()) {
                    diskSpaceGained += thisSize
                }
            }
        }
        return _prettySize(diskSpaceGained)
    }

    def _prettySize(size) {

        def precision = 1

        if (size < 1024) {
            return "${size}B"
        }
        if (size < 1024*1024) {
            return String.format("%.${precision}fKb", size / 1024.0)
        }
        if (size < 1024*1024*1024) {
            return String.format("%.${precision}fMb", size/ (1024.0 * 1024.0))
        }
        return String.format("%.${precision}fGig", size/ (1024.0 * 1024.0 * 1024.0))
    }
}
