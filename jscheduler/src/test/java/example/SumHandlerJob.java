package example;

import benchmark.BenchMarkTest;
import benchmark.BenchMarkTestManager;
import job.Job;
import job.JobContainer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SumHandlerJob extends JobContainer {

    private final boolean isLeft;
    private final int sumLimit;

    public SumHandlerJob(Job sumHandlerJob, boolean isLeft, int sumLimit) {
        setJob(sumHandlerJob);

        this.isLeft = isLeft;
        this.sumLimit = sumLimit;
    }

    public void init() {
        getJob().setRunnable(() -> {
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
                    log.info("totalLeftExecutionTime: {}", BenchMarkTestManager.getInstance().getTotalRightExecutionTimeMilli());
                }
            }
        });
    }

}
