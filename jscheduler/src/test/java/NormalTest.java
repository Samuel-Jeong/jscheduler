import example.SendRtpJob;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedule.ScheduleManager;

import java.util.concurrent.TimeUnit;

public class NormalTest {

    private static final Logger logger = LoggerFactory.getLogger(NormalTest.class);

    @Test
    public void test() {
        String scheduleKey = "THREAD_RTP";
        String jobKey = "SEND_RTP_JOB";

        ScheduleManager scheduleManager = ScheduleManager.getInstance();

        // 1) Init job
        scheduleManager.initJob(scheduleKey, 10);

        // 2) Add job
        if (scheduleManager.addJob(scheduleKey, new SendRtpJob(jobKey, 0, 1000, TimeUnit.MILLISECONDS))) {
            logger.debug("Success add job. (key={})", scheduleKey);
        }

        // 3) Wait for processing the job
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 4) Remove job
        if (scheduleManager.removeJob(scheduleKey, jobKey)) {
            logger.debug("Success remove job. (scheduleKey={}, jobKey={})", scheduleKey, jobKey);
        }

        // 5) Stop job
        if (scheduleManager.stopJob(scheduleKey)) {
            logger.debug("Success stop job. (key={})", scheduleKey);
        }
    }

}
