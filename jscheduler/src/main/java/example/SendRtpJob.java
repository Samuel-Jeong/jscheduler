package example;

import job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SendRtpJob extends Job {

    private static final Logger logger = LoggerFactory.getLogger(SendRtpJob.class);

    public SendRtpJob(String name, int initialDelay, int interval, TimeUnit timeUnit) {
        super(name, initialDelay, interval, timeUnit);
    }

    @Override
    public void run() {
        logger.debug("SEND RTP !!!");
    }

}
