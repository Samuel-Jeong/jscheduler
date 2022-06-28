package benchmark;

import example.SumHandlerJob;
import example.SumHandlerRunnable;
import job.Job;
import job.JobBuilder;
import lombok.extern.slf4j.Slf4j;
import schedule.ScheduleManager;

import java.util.concurrent.TimeUnit;

@Slf4j
public class JSchedulerTest implements SchedulerFactory {

    private final ScheduleManager scheduleManager;
    private static final String SCHEDULE_KEY = "NORMAL_TEST";

    public JSchedulerTest(int poolSize) {
        this.scheduleManager = new ScheduleManager();

        if (scheduleManager.initJob(SCHEDULE_KEY, poolSize, poolSize)) {
            log.debug("Success to initialize the schedule unit. (key={})", SCHEDULE_KEY);
        }
    }

    @Override
    public void startOneShotForCount(int count) {
        for (int i = 0; i < count; i++) {
            Job sumHandlerJob = new JobBuilder()
                    .setScheduleManager(scheduleManager)
                    .setName(SumHandlerJob.class.getSimpleName() + "_" + i)
                    .setInitialDelay(0)
                    .setInterval(1)
                    .setTimeUnit(TimeUnit.SECONDS)
                    .setPriority(1)
                    .setTotalRunCount(1)
                    .setIsLasted(false)
                    .build();
            SumHandlerJob sumHandler = new SumHandlerJob(sumHandlerJob, false, 100000);
            sumHandler.init();

            scheduleManager.startJob(SCHEDULE_KEY, sumHandler.getJob());
        }
    }

    @Override
    public void startAtFixedRate() {

    }

    @Override
    public void stop() {
        scheduleManager.stopAll(SCHEDULE_KEY);
    }

}
