package schedule.unit;

import job.base.Job;
import job.JobUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedule.handler.NonPriorityJobHandler;
import schedule.handler.PriorityJobHandler;

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

    public boolean addJobUnit(Job job, int poolSize, int dataStructureSize, boolean isPriorityScheduled) {
        if (job == null) {
            return false;
        }

        String jobUnitKey = scheduleUnitKey + "_" + job.getName();
        JobUnit jobUnit = jobUnitMap.get(jobUnitKey);
        if (jobUnit == null) {
            if (isPriorityScheduled) {
                jobUnit = new JobUnit(jobUnitKey,
                        new PriorityJobHandler(
                                poolSize, dataStructureSize
                        )
                );
            } else {
                jobUnit = new JobUnit(jobUnitKey,
                        new NonPriorityJobHandler(
                                poolSize,
                                dataStructureSize
                        )
                );
            }
            jobUnitMap.put(jobUnitKey, jobUnit);
        }

        if (jobUnit.start(job)) {
            logger.debug("({}) Job is added. (job={})", scheduleUnitKey, job);
            return true;
        }

        return false;
    }

    public boolean removeJobUnit(String scheduleUnitKey, Job job) {
        if (scheduleUnitKey == null || job == null) {
            return false;
        }

        String jobUnitKey = this.scheduleUnitKey + "_" + job.getName();
        JobUnit jobUnit = jobUnitMap.get(jobUnitKey);
        if (jobUnit == null) {
            return false;
        }

        if (jobUnit.stop(job)) {
            logger.debug("[{}] Job is canceled. (job={})", this.scheduleUnitKey, job);
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
