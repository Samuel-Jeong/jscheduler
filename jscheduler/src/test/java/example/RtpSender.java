package example;

import job.Job;
import job.JobContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtpSender extends JobContainer {

    private static final Logger logger = LoggerFactory.getLogger(RtpSender.class);

    public RtpSender(Job sendRtpJob) {
        setJob(sendRtpJob);
    }

    public void init() {
        getJob().setRunnable(() -> logger.debug("SEND RTP !!!"));
    }

}
