import example.HaHandler;
import example.RtpRecevier;
import example.RtpSender;
import job.Job;
import job.JobBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedule.ScheduleManager;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NormalTest {

    @Test
    public void test() {
        String scheduleKey = "NORMAL_TEST";

        ScheduleManager scheduleManager = new ScheduleManager();

        // 1) Init job
        if (scheduleManager.initJob(scheduleKey, 10, 10)) {
            log.debug("Success to initialize the schedule unit. (key={})", scheduleKey);
        }

        // 2) Add job
        Job rtpSendJob = new JobBuilder()
                .setScheduleManager(scheduleManager)
                .setName(RtpSender.class.getSimpleName())
                .setInitialDelay(0)
                .setInterval(1000)
                .setTimeUnit(TimeUnit.MILLISECONDS)
                .setPriority(1)
                .setTotalRunCount(1)
                .setIsLasted(true)
                .build();
        RtpSender rtpSender = new RtpSender(rtpSendJob);
        rtpSender.init();
        if (scheduleManager.startJob(scheduleKey, rtpSender.getJob())) {
            log.debug("Success to add the job. (key={}, rtpSendJob={})", scheduleKey, rtpSender.getJob());
        }

        Job haHandleJob = new JobBuilder()
                .setScheduleManager(scheduleManager)
                .setName(HaHandler.class.getSimpleName())
                .setInitialDelay(0)
                .setInterval(1000)
                .setTimeUnit(TimeUnit.MILLISECONDS)
                .setPriority(3)
                .setTotalRunCount(1)
                .setIsLasted(true)
                .build();
        HaHandler haHandler = new HaHandler(haHandleJob);
        haHandler.init();
        if (scheduleManager.startJob(scheduleKey, haHandler.getJob())) {
            log.debug("Success to add the job. (key={}, haHandleJob={})", scheduleKey, haHandler.getJob());
        }

        Job rtpRecvJob = new JobBuilder()
                .setScheduleManager(scheduleManager)
                .setName(RtpRecevier.class.getSimpleName())
                .setInitialDelay(0)
                .setInterval(1000)
                .setTimeUnit(TimeUnit.MILLISECONDS)
                .setPriority(5)
                .setTotalRunCount(1)
                .setIsLasted(true)
                .build();
        RtpRecevier rtpReceiver = new RtpRecevier(rtpRecvJob);
        rtpReceiver.init();
        if (scheduleManager.startJob(scheduleKey, rtpReceiver.getJob())) {
            log.debug("Success to add the job. (key={}, rtpRecvJob={})", scheduleKey, rtpReceiver.getJob());
        }

        // 3) Wait for processing the job
        TimeUnit msTimeUnit = TimeUnit.MILLISECONDS;
        try {
            msTimeUnit.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 4) Stop job
        scheduleManager.stopJob(scheduleKey, rtpSender.getJob());
        scheduleManager.stopJob(scheduleKey, haHandler.getJob());
        scheduleManager.stopJob(scheduleKey, rtpReceiver.getJob());

        // 5) Stop all
        scheduleManager.stopAll(scheduleKey);
    }

}
