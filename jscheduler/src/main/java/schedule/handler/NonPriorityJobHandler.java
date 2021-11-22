package schedule.handler;

import job.base.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class NonPriorityJobHandler extends JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(NonPriorityJobHandler.class);

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;
    private final Map<String, ScheduledFuture<?>> jobMap;

    public NonPriorityJobHandler(int poolSize, int mapSize) {
        super(poolSize, mapSize);

        jobMap = new ConcurrentHashMap<>(mapSize);
    }

    @Override
    public boolean start(Job job) {
        if (job == null) {
            return false;
        }

        if (scheduledThreadPoolExecutor == null) {
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(poolSize);
        }

        ScheduledFuture<?> scheduledFuture;
        try {
            scheduledFuture = scheduledThreadPoolExecutor.scheduleAtFixedRate(
                    job,
                    job.getInitialDelay(),
                    job.getInterval(),
                    job.getTimeUnit()
            );
        } catch (Exception e) {
            logger.warn("NonPriorityJobHandler.addJob.Exception", e);
            return false;
        }

        return jobMap.putIfAbsent(job.getName(), scheduledFuture) == null;
    }

    @Override
    public boolean stop(Job job) {
        String jobKey = job.getName();
        if (jobKey == null) {
            return false;
        }

        ScheduledFuture<?> scheduledFuture = jobMap.get(jobKey);
        if (scheduledFuture == null) {
            return false;
        }

        return scheduledFuture.cancel(true);
    }

    @Override
    public void stopAll() {
        for (Map.Entry<String, ScheduledFuture<?>> entry : jobMap.entrySet()) {
            if (entry == null) {
                continue;
            }

            ScheduledFuture<?> scheduledFuture = entry.getValue();
            if (scheduledFuture == null) {
                continue;
            }

            scheduledFuture.cancel(true);
        }

        if (scheduledThreadPoolExecutor != null) {
            scheduledThreadPoolExecutor.shutdown();
            scheduledThreadPoolExecutor = null;
        }
    }

    @Override
    public String toString() {
        return "NonPriorityJobHandler{" +
                "poolSize=" + poolSize +
                ", mapTotalSize=" + dataStructureSize +
                ", jobSize=" + jobMap.size() +
                '}';
    }
}
