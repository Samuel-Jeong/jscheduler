package schedule.unit;

import job.Job;
import schedule.handler.JobScheduler;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class JobAdder implements Runnable {

    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    private final JobScheduler jobScheduler;
    private final Job job;
    private final int executorIndex;

    public JobAdder(JobScheduler jobScheduler, Job job, int executorIndex) {
        this.jobScheduler = jobScheduler;
        this.job = job;
        this.executorIndex = executorIndex;
    }

    @Override
    public void run() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(
                !job.isLasted() ?
                        (() -> {
                            if (isJobFinished(job)) {
                                jobScheduler.cancel(job);
                            } else {
                                job.decCurRemainRunCount();
                                jobScheduler.addJobToExecutor(executorIndex, job);
                            }
                        })
                        :
                        (() -> {
                            if (isJobFinished(job)) {
                                jobScheduler.cancel(job);
                            } else {
                                jobScheduler.addJobToExecutor(executorIndex, job);
                            }
                        }),
                job.getInitialDelay(), job.getInterval(), job.getTimeUnit()
        );
    }

    public void stop() {
        scheduledThreadPoolExecutor.shutdown();
    }

    public boolean isJobFinished(Job job) {
        if (job == null) {
            return true;
        }

        return job.getIsFinished() ||
                (!job.isLasted() && (job.decCurRemainRunCount() < 0));
    }

}
