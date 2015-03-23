package au.org.emii.gogoduck.job

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.BlockingQueue

import org.slf4j.Logger
import org.slf4j.LoggerFactory

// Note: this queue is unbounded - but this is the same as the previous behaviour
// using the quartz scheduler.
class JobQueue extends LinkedBlockingQueue<Map> implements Runnable {
    void run() {
        final Logger log = LoggerFactory.getLogger(JobQueue)

        while (true) {
            try {
                log.debug "Waiting for job..."
                def jobContext = this.take()
                def job = jobContext.job
                def executor = jobContext.executor

                log.debug "Job taken, queue size: ${JOB_QUEUE.size()}"

                executor.run(job)
            }
            catch (InterruptedException e) {
                log.error("Error handling job", e)
            }
        }
    }

    def getQueuePosition(job) {
        return this.findIndexOf {
            it.job?.uuid == job.uuid
        }
    }
}
