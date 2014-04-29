package au.org.emii.gogoduck.job

class JobStoreService {
    def grailsApplication

    Job get(uuid) {
        try {
            log.debug("file: ${getJsonPathForId(uuid)}, text: ${getFile(getJsonPathForId(uuid)).text}")
            return Job.fromJsonString(getFile(getJsonPathForId(uuid)).text)
        }
        catch (Exception e) {
            log.warn("Invalid or corrupt job with ID: ${uuid}", e)
            return null
        }
    }

    void save(job) {
        log.info("Saving job: ${job.toString()}")
        makeDir(job)
        writeToFileAsJson(job)
    }

    void delete(jobs) {
        removeNulls(jobs).each {
            log.info("Deleting job: ${it.toString()}")
            rmDir(it)
        }
    }

    List<Job> list() {
        removeNulls(
            listUuids().collect {
                get(it)
            }
        )
    }

    String getAggrPath(job) {
        getAggrPathForId(job.uuid)
    }

    File getAggrFile(job) {
        log.debug("Job path: ${getAggrPath(job)}")
        getFile(getAggrPath(job))
    }

    File getReportFile(job) {
        log.debug("Job path: ${getAggrPath(job)}")
        getFile(getPath(job) + File.separator + "report.txt")
    }

    void makeDir(job) {
        log.debug("Making directory: ${getPath(job)}")
        getFile(getPath(job)).mkdirs()
    }

    void rmDir(job) {
        log.debug("Removing directory: ${getPath(job)}")
        getFile(getPath(job)).deleteDir()
    }

    void writeToFileAsJson(job) {
        log.debug("Job: ${job.toJsonString()}")
        getFile(getJsonPathForId(job.uuid)).write(job.toJsonString())
    }

    private String getPath(job) {
        getPathForId(job.uuid)
    }

    private String getPathForId(jobId) {
        "${grailsApplication.config.worker.outputPath}${File.separator}${jobId}"
    }

    private String getAggrPathForId(jobId) {
        "${getPathForId(jobId)}${File.separator}${grailsApplication.config.worker.outputFilename}"
    }

    String getJsonPathForId(jobId) {
        "${getPathForId(jobId)}${File.separator}job.json"
    }

    List<String> listUuids() {
        new File(grailsApplication.config.worker.outputPath).list().toList()
    }

    File getFile(path) {
        new File(path)
    }

    List<Job> removeNulls(jobs) {
        jobs.grep { it != null }
    }
}
