package schedule.unit;

import job.Job;
import job.JobUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ScheduleUnit {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleUnit.class);

    public static final int DEFAULT_THREAD_COUNT = 5;

    private final String scheduleUnitKey;
    private final long createdTime;
    private final int threadCount;

    ////////////////////////////////////////////////////////////////////////////////

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

    private final Map<String, JobUnit> jobUnitMap = new ConcurrentHashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    public ScheduleUnit(String key, int threadCount) {
        this.scheduleUnitKey = key;
        this.createdTime = System.currentTimeMillis();

        if (threadCount > 0) {
            this.threadCount = threadCount;
        } else {
            this.threadCount = DEFAULT_THREAD_COUNT;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void start() {
        if (scheduledThreadPoolExecutor == null) {
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(threadCount);
        }
    }

    public void stop() {
        for (JobUnit jobUnit : jobUnitMap.values()) {
            jobUnit.stopAll();
        }

        scheduledThreadPoolExecutor.shutdown();
        scheduledThreadPoolExecutor = null;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean addJobUnit(Job job) {
        if (scheduledThreadPoolExecutor == null || job == null) {
            return false;
        }

        String jobUnitKey = scheduleUnitKey + "_" + job.getName();
        JobUnit jobUnit = jobUnitMap.get(jobUnitKey);
        if (jobUnit == null) {
            jobUnit = new JobUnit(jobUnitKey);
        }

        if (jobUnit.addJob(job.getName(),
                scheduledThreadPoolExecutor.scheduleAtFixedRate(
                        job, job.getInterval(), job.getInterval(), job.getTimeUnit()))) {
            logger.debug("({}) Job is added. (name={}, interval={}({}))",
                    scheduleUnitKey, job.getName(), job.getInterval(), job.getTimeUnit().name()
            );

            jobUnitMap.put(jobUnitKey, jobUnit);
            return true;
        }

        return false;
    }

    public boolean removeJobUnit(String scheduleUnitKey, String jobKey) {
        if (scheduleUnitKey == null || jobKey == null) {
            return false;
        }

        String jobUnitKey = this.scheduleUnitKey + "_" + jobKey;
        JobUnit jobUnit = jobUnitMap.get(jobUnitKey);
        if (jobUnit == null) {
            return false;
        }

        if (jobUnit.stop(jobKey)) {
            logger.debug("[{}] Job is canceled. (jobKey={})", this.scheduleUnitKey, jobKey);
        }

        return jobUnitMap.remove(jobUnitKey) != null;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String getScheduleUnitKey() {
        return scheduleUnitKey;
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
                "key='" + scheduleUnitKey + '\'' +
                ", createdTime=" + createdTime +
                ", threadCount=" + threadCount +
                '}';
    }
}
