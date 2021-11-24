import example.HaHandler;
import example.RecvRtpJob;
import example.SendRtpJob;
import job.Job;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedule.ScheduleManager;

import java.util.concurrent.TimeUnit;

public class NormalTest {

    private static final Logger logger = LoggerFactory.getLogger(NormalTest.class);

    @Test
    public void test() {
        String scheduleKey = "NORMAL_TEST";
        String sendRtpJobKey = "SEND_RTP_JOB";
        String haHandlerJobKey = "HA_HANDLER";
        String recvRtpJobKey = "RECV_RTP_JOB";

        ScheduleManager scheduleManager = ScheduleManager.getInstance();

        // 1) Init job
        if (scheduleManager.initJob(scheduleKey, 10, 10)) {
            logger.debug("Success to initialize the schedule unit. (key={})", scheduleKey);
        }

        // 2) Add job
        Job sendRtpJob = new SendRtpJob(sendRtpJobKey,
                0, 1000, TimeUnit.MILLISECONDS,
                1, 0, true
        );
        if (scheduleManager.startJob(scheduleKey, sendRtpJob)) {
            logger.debug("Success to add the job. (key={}, sendRtpJob={})", scheduleKey, sendRtpJob);
        }

        Job haHandlerJob = new HaHandler(haHandlerJobKey,
                0, 1000, TimeUnit.MILLISECONDS,
                3, 0, true
        );
        if (scheduleManager.startJob(scheduleKey, haHandlerJob)) {
            logger.debug("Success to add the job. (key={}, haHandlerJob={})", scheduleKey, haHandlerJob);
        }

        Job recvRtpJob = new RecvRtpJob(recvRtpJobKey,
                0, 1000, TimeUnit.MILLISECONDS,
                5, 0, true
        );
        if (scheduleManager.startJob(scheduleKey, recvRtpJob)) {
            logger.debug("Success to add the job. (key={}, recvRtpJob={})", scheduleKey, recvRtpJob);
        }

        // 3) Wait for processing the job
        TimeUnit msTimeUnit = TimeUnit.MILLISECONDS;
        try {
            msTimeUnit.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 4) Stop job
        scheduleManager.stopJob(scheduleKey, sendRtpJob);
        scheduleManager.stopJob(scheduleKey, haHandlerJob);
        scheduleManager.stopJob(scheduleKey, recvRtpJob);

        // 5) Stop all
        scheduleManager.stopAll(scheduleKey);
    }

}
