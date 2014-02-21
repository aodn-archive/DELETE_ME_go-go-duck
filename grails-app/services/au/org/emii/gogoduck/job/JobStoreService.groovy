package au.org.emii.gogoduck.job

class JobStoreService {
    def grailsApplication

    void makeDir(job) {
        log.debug("Making directory: ${getDir(job)}")
        new File(getDir(job)).mkdirs()
    }

    String getDir(job) {
        "${grailsApplication.config.worker.outputPath}${File.separator}${job.getUuid()}"
    }

    String getAggrPath(job) {
        "${getDir(job)}${File.separator}${job.getUuid()}"
    }

    void writeToFileAsJson(job) {
        log.debug("Job: ${job.toJsonString()}")
        new File("${getDir(job)}${File.separator}job.json").write(job.toJsonString())
    }
}