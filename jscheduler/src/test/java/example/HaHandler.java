package example;

import job.Job;
import job.JobContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HaHandler extends JobContainer {

    private static final Logger logger = LoggerFactory.getLogger(HaHandler.class);

    public HaHandler(Job haHandleJob) {
        setJob(haHandleJob);
    }

    public void init() {
        getJob().setRunnable(() -> logger.debug("HA: THREAD={}", Thread.activeCount()));
    }

}
