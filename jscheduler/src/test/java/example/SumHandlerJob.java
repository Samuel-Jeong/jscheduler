package example;

import benchmark.BenchMarkTestManager;
import job.Job;
import job.JobContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SumHandlerJob extends JobContainer {

    private static final Logger logger = LoggerFactory.getLogger(SumHandlerJob.class);

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
                BenchMarkTestManager.getInstance().incAndGetLeftExecutionCount();
            } else {
                BenchMarkTestManager.getInstance().incAndGetRightExecutionCount();
            }
        });
    }

}
