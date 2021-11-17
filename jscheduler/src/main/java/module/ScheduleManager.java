package module;

import base.ScheduleUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public ScheduleUnit addScheduleUnit(String key, int threadCount, long delay) {
        if (key == null) {
            return null;
        }

        try {
            scheduleUnitMapLock.lock();

            scheduleUnitMap.putIfAbsent(key,
                    new ScheduleUnit(
                            key,
                            threadCount,
                            delay
                    )
            );
            return scheduleUnitMap.get(key);
        } catch (Exception e) {
            logger.warn("Fail to add the call info.", e);
            return null;
        } finally {
            scheduleUnitMapLock.unlock();
        }
    }

    public ScheduleUnit deleteScheduleUnit(String key) {
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

    public ScheduleUnit getScheduleUnit(String callId) {
        if (callId == null) { return null; }

        return scheduleUnitMap.get(callId);
    }

    public void clearCallInfoMap() {
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


}
