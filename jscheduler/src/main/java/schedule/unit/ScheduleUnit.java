package schedule.unit;

import job.Job;
import job.JobUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScheduleUnit {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleUnit.class);

    public static final int DEFAULT_THREAD_COUNT = 5;

    private final String scheduleUnitKey;
    private final int threadCount; // JobUnit count

    ////////////////////////////////////////////////////////////////////////////////

    private final Map<String, JobUnit> jobUnitMap;

    ////////////////////////////////////////////////////////////////////////////////

    public ScheduleUnit(String key, int threadCount) {
        this.scheduleUnitKey = key;

        if (threadCount > 0) {
            this.threadCount = threadCount;
        } else {
            this.threadCount = DEFAULT_THREAD_COUNT;
        }

        jobUnitMap = new ConcurrentHashMap<>(this.threadCount);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void stop() {
        for (JobUnit jobUnit : jobUnitMap.values()) {
            jobUnit.stopAll();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean addJobUnit(Job job, int threadPoolCount) {
        if (job == null) {
            return false;
        }

        String jobUnitKey = scheduleUnitKey + "_" + job.getName();
        JobUnit jobUnit = jobUnitMap.get(jobUnitKey);
        if (jobUnit == null) {
            jobUnit = new JobUnit(jobUnitKey, threadPoolCount);
            jobUnitMap.put(jobUnitKey, jobUnit);
        }

        if (jobUnit.start(job)) {
            logger.debug("({}) Job is added. (name={}, interval={}({}))",
                    scheduleUnitKey, job.getName(), job.getInterval(), job.getTimeUnit().name()
            );
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

    public int getThreadCount() {
        return threadCount;
    }

    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "ScheduleUnit{" +
                "key='" + scheduleUnitKey + '\'' +
                ", threadCount=" + threadCount +
                '}';
    }
}
