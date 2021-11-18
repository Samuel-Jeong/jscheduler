package job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class JobUnit {

    private static final Logger logger = LoggerFactory.getLogger(JobUnit.class);

    private final String name;
    private final Map<String, ScheduledFuture<?>> jobMap = new ConcurrentHashMap<>();
    ////////////////////////////////////////////////////////////////////////////////

    public JobUnit(String name) {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean addJob(String key, ScheduledFuture<?> scheduledFuture) {
        if (scheduledFuture == null) {
            return false;
        }

        return jobMap.putIfAbsent(key, scheduledFuture) == null;
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
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

}
