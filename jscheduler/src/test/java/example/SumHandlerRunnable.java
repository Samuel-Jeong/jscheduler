package example;

import benchmark.BenchMarkTest;
import benchmark.BenchMarkTestManager;
import benchmark.SchedulerFactory;
import job.Job;
import job.JobContainer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Slf4j
public class SumHandlerRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SumHandlerRunnable.class);

    private final boolean isLeft;
    private final int sumLimit;

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public SumHandlerRunnable(ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, boolean isLeft, int sumLimit) {
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
        this.isLeft = isLeft;
        this.sumLimit = sumLimit;
    }

    @Override
    public void run() {
        if (sumLimit <= 0) { return; }

        List<Integer> sumList = new ArrayList<>();
        for (int i = 0; i < sumLimit; i++) {
            sumList.add(i);
        }

        if (isLeft) {
            long count = BenchMarkTestManager.getInstance().incAndGetLeftExecutionCount();
            if (count == BenchMarkTest.SCHEDULE_COUNT) {
                BenchMarkTestManager.getInstance().stopLeftExecutionTimer();
                log.info("totalLeftExecutionTime: {}", BenchMarkTestManager.getInstance().getTotalLeftExecutionTimeMilli());
            }
        } else {
            long count = BenchMarkTestManager.getInstance().incAndGetRightExecutionCount();
            if (count == BenchMarkTest.SCHEDULE_COUNT) {
                BenchMarkTestManager.getInstance().stopRightExecutionTimer();
                log.info("totalRightExecutionTime: {}", BenchMarkTestManager.getInstance().getTotalRightExecutionTimeMilli());
            }
        }

        /*logger.info("{}", sumList.stream().count());
        scheduledThreadPoolExecutor.remove(this);
        scheduledThreadPoolExecutor.purge();*/
        //scheduledThreadPoolExecutor.shutdownNow();
        //scheduledThreadPoolExecutor.remove(this);
    }

}
