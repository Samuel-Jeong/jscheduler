package schedule.unit;

import job.Job;
import job.JobUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ScheduleUnit {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleUnit.class);

    public static final int DEFAULT_THREAD_COUNT = 5;

    private final String key;
    private final long createdTime;
    private final int threadCount;

    ////////////////////////////////////////////////////////////////////////////////

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

    private final Map<String, JobUnit> jobUnitMap = new ConcurrentHashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    public ScheduleUnit(String key, int threadCount) {
        this.key = key;
        this.createdTime = System.currentTimeMillis();
        this.threadCount = threadCount;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void start() {
        if (scheduledThreadPoolExecutor == null) {
            if (threadCount > 0) {
                scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(threadCount);
            } else {
                scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(DEFAULT_THREAD_COUNT);
            }
        }
    }

    public void stop() {
        if (scheduledThreadPoolExecutor != null) {
            for (JobUnit jobUnit : jobUnitMap.values()) {
                jobUnit.stop();
            }

            scheduledThreadPoolExecutor.shutdown();
            scheduledThreadPoolExecutor = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean addJobUnit(Job job) {
        if (scheduledThreadPoolExecutor == null || job == null) {
            return false;
        }

        String jobUnitName = key + "_" + job.getName();
        JobUnit jobUnit = jobUnitMap.get(jobUnitName);
        if (jobUnit == null) {
            jobUnit = new JobUnit(jobUnitName);
        }

        if (
                jobUnit.addJob(
                        job.getName(),
                        scheduledThreadPoolExecutor.scheduleAtFixedRate(
                                job,
                                job.getInterval(),
                                job.getInterval(),
                                job.getTimeUnit()
                        )
                )
        ) {
            logger.debug("({}) Job is added. (name={}, interval={}({}))",
                    key, job.getName(), job.getInterval(), job.getTimeUnit().name()
            );

            jobUnitMap.put(jobUnitName, jobUnit);
            return true;
        }

        return false;
    }

    public boolean removeJobUnit(String key) {
        if (key == null) {
            return false;
        }

        return jobUnitMap.remove(key) != null;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String getKey() {
        return key;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public int getThreadCount() {
        return threadCount;
    }

    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "ScheduleUnit{" +
                "key='" + key + '\'' +
                ", createdTime=" + createdTime +
                ", threadCount=" + threadCount +
                '}';
    }
}
