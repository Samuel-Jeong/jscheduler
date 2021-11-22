package schedule;

import job.base.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedule.unit.ScheduleUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ScheduleManager {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleManager.class);
    
    private static ScheduleManager scheduleManager = null;

    private final HashMap<String, ScheduleUnit> scheduleUnitMap = new HashMap<>();
    private final ReentrantLock scheduleUnitMapLock = new ReentrantLock();

    ////////////////////////////////////////////////////////////////////////////////

    public ScheduleManager() {
        // ignore
    }
    
    public static ScheduleManager getInstance() {
        if (scheduleManager == null) {
            scheduleManager = new ScheduleManager();
        }

        return scheduleManager;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public int getScheduleUnitMapSize() {
        return scheduleUnitMap.size();
    }

    public Map<String, ScheduleUnit> getCloneCallMap( ) {
        HashMap<String, ScheduleUnit> cloneMap;

        try {
            scheduleUnitMapLock.lock();

            cloneMap = (HashMap<String, ScheduleUnit>) scheduleUnitMap.clone();
        } catch (Exception e) {
            logger.warn("Fail to clone the schedule unit map.",  e);
            cloneMap = scheduleUnitMap;
        } finally {
            scheduleUnitMapLock.unlock();
        }

        return cloneMap;
    }

    private ScheduleUnit addScheduleUnit(String key, int threadCount) {
        if (key == null) {
            return null;
        }

        try {
            scheduleUnitMapLock.lock();

            scheduleUnitMap.putIfAbsent(key,
                    new ScheduleUnit(
                            key,
                            threadCount
                    )
            );
            return scheduleUnitMap.get(key);
        } catch (Exception e) {
            logger.warn("Fail to add the schedule unit.", e);
            return null;
        } finally {
            scheduleUnitMapLock.unlock();
        }
    }

    private ScheduleUnit removeScheduleUnit(String key) {
        if (key == null) { return null; }

        try {
            scheduleUnitMapLock.lock();

            return scheduleUnitMap.remove(key);
        } catch (Exception e) {
            logger.warn("Fail to delete the schedule unit map.", e);
            return null;
        } finally {
            scheduleUnitMapLock.unlock();
        }
    }

    public ScheduleUnit getScheduleUnit(String key) {
        if (key == null) { return null; }

        return scheduleUnitMap.get(key);
    }

    public void clearScheduleUnitMap() {
        try {
            scheduleUnitMapLock.lock();

            scheduleUnitMap.clear();
            logger.debug("Success to clear the schedule unit map.");
        } catch (Exception e) {
            logger.warn("Fail to clear the schedule unit map.", e);
        } finally {
            scheduleUnitMapLock.unlock();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean initJob(String key, int threadPoolCount) {
        return addScheduleUnit(key, threadPoolCount) != null;
    }

    public boolean addJob(String key, Job job, int poolSize, int queueSize, boolean isPriorityScheduled) {
        if (key == null) { return false; }

        ScheduleUnit scheduleUnit = getScheduleUnit(key);
        if (scheduleUnit == null) {
            return false;
        }

        return scheduleUnit.addJobUnit(job, poolSize, queueSize, isPriorityScheduled);
    }

    public boolean removeJob(String scheduleUnitKey, Job job) {
        if (scheduleUnitKey == null || job == null) { return false; }

        ScheduleUnit scheduleUnit = getScheduleUnit(scheduleUnitKey);
        if (scheduleUnit == null) {
            return false;
        }

        return scheduleUnit.removeJobUnit(scheduleUnitKey, job);
    }

    public boolean stopJob(String scheduleUnitKey) {
        ScheduleUnit scheduleUnit = getScheduleUnit(scheduleUnitKey);
        if (scheduleUnit == null) {
            return false;
        }

        scheduleUnit.stop();
        return removeScheduleUnit(scheduleUnitKey) != null;
    }

    ////////////////////////////////////////////////////////////////////////////////

}
