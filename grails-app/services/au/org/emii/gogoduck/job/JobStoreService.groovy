package au.org.emii.gogoduck.job

class JobStoreService {
    def grailsApplication

    void makeDir(job) {
        log.debug("Making directory: ${getDir(job)}")
        new File(getDir(job)).mkdirs()
    }

    String getDir(job) {
        getDirForId(job.uuid)
    }

    // TODO: Deal only with jobs (not IDs)?
    String getDirForId(jobId) {
        "${grailsApplication.config.worker.outputPath}${File.separator}${jobId}"
    }

    String getAggrPath(job) {
        getAggrPathForId(job.uuid)
    }

    String getAggrPathForId(jobId) {
        "${getDirForId(jobId)}${File.separator}${grailsApplication.config.worker.outputFilename}"
    }

    File getAggrFile(jobId) {
        log.info("File path: ${getAggrPathForId(jobId)}")
        def aggrFile = new File(getAggrPathForId(jobId))
        log.info("aggrFile.name = ${aggrFile.name}")
        return aggrFile
    }

    void writeToFileAsJson(job) {
        log.debug("Job: ${job.toJsonString()}")
        new File("${getDir(job)}${File.separator}job.json").write(job.toJsonString())
    }
}