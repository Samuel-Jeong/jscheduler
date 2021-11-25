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
    private final int queueSize;

    private final JobExecutor[] jobExecutors; // Round-Robin executor selection
    private int curExecutorIndex = 0;
    private final Timer timer = new Timer(true);

    ////////////////////////////////////////////////////////////////////////////////

    public JobScheduler(String ownerName, int poolSize, int queueSize) {
        this.ownerName = ownerName;
        this.poolSize = poolSize;
        this.queueSize = queueSize;

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

        if (isJobFinished(job)) {
            return false;
        }

        curExecutorIndex = addJobToExecutor(curExecutorIndex, job);
        int interval = job.getInterval();
        if (interval > 0) {
            timer.scheduleAtFixedRate(new FutureScheduler(curExecutorIndex, job, timer), interval, interval);
        }

        return true;
    }

    public void cancel(Job job) {
        job.setIsFinished(true);
    }

    public void stop() {
        for (int i = 0; i < poolSize; i++) {
            jobExecutors[i].stop();
        }

        timer.cancel(); // daemon 이어도 안전하게 종료
        logger.debug("[{}] is finished.", ownerName);
    }

    ////////////////////////////////////////////////////////////////////////////////

    private int addJobToExecutor(int index, Job job) {
        jobExecutors[index].addJob(job);

        index++;
        if (index >= poolSize) {
            index = 0;
        }

        return index;
    }

    private boolean isJobFinished(Job job) {
        if (job == null) {
            return true;
        }

        return job.getIsFinished() ||
                (!job.isLasted() && (job.decCurRemainRunCount() < 0));
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
            if (isJobFinished(job)) {
                return;
            }

            int newIndex = addJobToExecutor(index, job);
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

    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "JobScheduler{" +
                "ownerName='" + ownerName + '\'' +
                ", poolSize=" + poolSize +
                ", queueSize=" + queueSize +
                '}';
    }
}
