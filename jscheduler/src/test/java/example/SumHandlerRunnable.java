package example;

import benchmark.BenchMarkTestManager;
import benchmark.SchedulerFactory;
import job.Job;
import job.JobContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
            BenchMarkTestManager.getInstance().incAndGetLeftExecutionCount();
        } else {
            BenchMarkTestManager.getInstance().incAndGetRightExecutionCount();
        }

        //logger.debug("{}", sumList.stream().count());
        //scheduledThreadPoolExecutor.shutdownNow();
        //scheduledThreadPoolExecutor.remove(this);
    }
}
