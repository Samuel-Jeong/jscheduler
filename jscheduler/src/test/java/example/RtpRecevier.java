package example;

import job.Job;
import job.JobContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RtpRecevier extends JobContainer {

    private static final Logger logger = LoggerFactory.getLogger(RtpRecevier.class);

    public RtpRecevier(Job rtpRecvJob) {
        setJob(rtpRecvJob);
    }

    public void init() {
        getJob().setRunnable(() -> logger.debug("RECV RTP @@@"));
    }

}
