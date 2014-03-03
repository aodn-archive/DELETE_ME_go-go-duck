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
        jobs.grep { it }.each {
            log.info("Deleting job: ${it.toString()}")
            rmDir(it)
        }
    }

    List<Job> list() {
        listUuids().collect {
            get(it)
        }.grep {
            it
        }
    }

    String getAggrPath(job) {
        getAggrPathForId(job.uuid)
    }

    File getAggrFile(job) {
        log.debug("File path: ${getAggrPath(job)}")
        getFile(getAggrPath(job))
    }

    void makeDir(job) {
        log.debug("Making directory: ${getDir(job)}")
        getFile(getDir(job)).mkdirs()
    }

    void rmDir(job) {
        log.debug("Removing directory: ${getDir(job)}")
        getFile(getDir(job)).deleteDir()
    }

    void writeToFileAsJson(job) {
        log.debug("Job: ${job.toJsonString()}")
        getFile(getJsonPathForId(job.uuid)).write(job.toJsonString())
    }

    private String getDir(job) {
        getDirForId(job.uuid)
    }

    private String getDirForId(jobId) {
        "${grailsApplication.config.worker.outputPath}${File.separator}${jobId}"
    }

    private String getAggrPathForId(jobId) {
        "${getDirForId(jobId)}${File.separator}${grailsApplication.config.worker.outputFilename}"
    }

    String getJsonPathForId(jobId) {
        "${getDirForId(jobId)}${File.separator}job.json"
    }

    List<String> listUuids() {
        new File(grailsApplication.config.worker.outputPath).list().toList()
    }

    File getFile(path) {
        new File(path)
    }
}
