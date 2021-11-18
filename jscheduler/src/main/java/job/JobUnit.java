package job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class JobUnit {

    private static final Logger logger = LoggerFactory.getLogger(JobUnit.class);

    private final String name;
    private final long createdTime;
    private final int threadPoolCount;
    private final Map<String, ScheduledFuture<?>> jobMap = new ConcurrentHashMap<>();

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

    ////////////////////////////////////////////////////////////////////////////////

    public JobUnit(String name, int threadPoolCount) {
        this.name = name;
        this.createdTime = System.currentTimeMillis();
        this.threadPoolCount = threadPoolCount;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean start(Job job) {
        if (job == null) {
            return false;
        }

        if (scheduledThreadPoolExecutor == null) {
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(threadPoolCount);
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
            logger.warn("JobUnit.addJob.Exception", e);
            return false;
        }

        return jobMap.putIfAbsent(job.getName(), scheduledFuture) == null;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean stop(String jobKey) {
        if (jobKey == null) {
            return false;
        }

        ScheduledFuture<?> scheduledFuture = jobMap.get(jobKey);
        if (scheduledFuture == null) {
            return false;
        }

        return scheduledFuture.cancel(true);
    }

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

    ////////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return "JobUnit{" +
                "name='" + name + '\'' +
                ", createdTime=" + createdTime +
                ", jobCount=" + jobMap.size() +
                '}';
    }
}
