
import example.SendRtpJob;
import schedule.ScheduleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedule.unit.ScheduleUnit;

import java.util.concurrent.TimeUnit;

public class JSchedulerMain {

    private static final Logger logger = LoggerFactory.getLogger(JSchedulerMain.class);

    public static void main(String[] args) {
        // TODO TEST

        ScheduleManager scheduleManager = ScheduleManager.getInstance();
        ScheduleUnit sendRtp0ScheduleUnit = scheduleManager.addScheduleUnit(
                "THREAD_0_SEND_RTP",
                100
        );
        logger.debug("sendRtp0ScheduleUnit: {}", sendRtp0ScheduleUnit);

        scheduleManager.startJob(sendRtp0ScheduleUnit.getKey());
        scheduleManager.addJob(sendRtp0ScheduleUnit.getKey(),
                new SendRtpJob(
                        "SendRtpJob",
                        1000,
                        TimeUnit.MILLISECONDS
                )
        );

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduleManager.stopJob(sendRtp0ScheduleUnit.getKey());
        scheduleManager.removeJob(sendRtp0ScheduleUnit.getKey());
        scheduleManager.removeScheduleUnit(sendRtp0ScheduleUnit.getKey());
    }

}
