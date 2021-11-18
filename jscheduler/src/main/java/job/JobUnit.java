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
    //private final ConcurrentCyclicFIFO<ScheduledFuture<?>> jobBuffer;

    ////////////////////////////////////////////////////////////////////////////////

    public JobUnit(String name) {
        this.name = name;
        //this.jobBuffer = new ConcurrentCyclicFIFO<>();
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean addJob(String key, ScheduledFuture<?> scheduledFuture) {
        if (scheduledFuture == null) {
            return false;
        }

        return jobMap.putIfAbsent(key, scheduledFuture) == null;
        //return jobBuffer.offer(scheduledFuture);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void stop() {
        /*while(true) {
            ScheduledFuture<?> scheduledFuture = jobBuffer.poll();
            if (scheduledFuture == null) {
                return;
            }

            scheduledFuture.cancel(true);
        }*/

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
