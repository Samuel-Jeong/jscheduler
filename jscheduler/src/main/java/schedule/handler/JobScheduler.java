package schedule.handler;

import job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class JobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    ////////////////////////////////////////////////////////////////////////////////

    private final String ownerName;
    private final int poolSize;
    private final JobExecutor[] jobExecutors; // Round-Robin executor selection
    private int curExecutorIndex = 0;
    private final Timer timer = new Timer(true);

    ////////////////////////////////////////////////////////////////////////////////

    public JobScheduler(String ownerName, int poolSize, int queueSize) {
        this.ownerName = ownerName;
        this.poolSize = poolSize;

        jobExecutors = new JobExecutor[poolSize];
        for (int i = 0; i < poolSize; i++) {
            jobExecutors[i] = new JobExecutor(i, queueSize);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean schedule(Job job) {
        int initialDelay = job.getInitialDelay();
        if (initialDelay > 0) {
            timer.scheduleAtFixedRate(new FutureScheduler(curExecutorIndex, job, timer), initialDelay, initialDelay);
            return true;
        }

        if (job.getIsFinished()) {
            return false;
        }

        if (!job.isLasted() && job.decCurRemainRunCount() < 0) {
            return false;
        }

        jobExecutors[curExecutorIndex].addJob(job);

        curExecutorIndex++;
        if (curExecutorIndex >= poolSize) {
            curExecutorIndex = 0;
        }

        int interval = job.getInterval();
        if (interval > 0) {
            timer.scheduleAtFixedRate(new FutureScheduler(curExecutorIndex, job, timer), interval, interval);
        }

        return true;
    }

    public void stop(Job job) {
        job.setIsFinished(true);
    }

    public void stopAll() {
        for (int i = 0; i < poolSize; i++) {
            jobExecutors[i].stop();
        }

        logger.debug("[{}({})] is finished.", ownerName, curExecutorIndex);
    }

    ////////////////////////////////////////////////////////////////////////////////

    private class FutureScheduler extends TimerTask {

        private final int index;
        private final Timer timer;
        private final Job job;

        public FutureScheduler(int index, Job job, Timer timer) {
            this.index = index;
            this.job = job;
            this.timer = timer;
        }

        @Override
        public void run() {
            if (job.getIsFinished()) {
                return;
            }

            if (!job.isLasted() && job.decCurRemainRunCount() < 0) {
                return;
            }

            jobExecutors[index].addJob(job);

            int newIndex = index + 1;
            if (newIndex >= poolSize) {
                newIndex = 0;
            }

            int interval = job.getInterval();
            if (interval > 0) {
                long intervalGap = System.currentTimeMillis() - this.scheduledExecutionTime();
                if (intervalGap > 0) {
                    timer.scheduleAtFixedRate(new FutureScheduler(newIndex, job, timer), interval - intervalGap, interval);
                } else {
                    timer.scheduleAtFixedRate(new FutureScheduler(newIndex, job, timer), interval, interval);
                }
            }

            this.cancel();
        }

    }

}
