package schedule.handler;

import job.Job;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConcurrentCyclicFIFO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class JobExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);

    private final int index;
    private final ConcurrentCyclicFIFO<Job> jobBuffer = new ConcurrentCyclicFIFO<>();
    private final ExecutorService executorService;
    private boolean isAlive = true;

    ////////////////////////////////////////////////////////////////////////////////

    public JobExecutor(int index) {
        this.index = index;

        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("JobExecutor" + "-" + index).build();
        executorService = Executors.newFixedThreadPool(1, threadFactory);
        executorService.execute(this::run);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void run() {
        while(isAlive) {
            try {
                Job job = jobBuffer.take();
                if (job == null) {
                    continue;
                }

                job.run();

                if (logger.isTraceEnabled()) {
                    logger.trace("[{}]-[{}]: get data ({})", index, job.getName(), job);
                }
            } catch (Exception e) {
                break;
            }
        }
    }

    public void stop() {
        isAlive = false;
        executorService.shutdown();
        jobBuffer.clear();
    }

    public void addJob(Job job) {
        jobBuffer.offer(job);
        if (logger.isTraceEnabled()) {
            logger.trace("[{}] add data ({})", index, job);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public int getIndex() {
        return index;
    }
}
