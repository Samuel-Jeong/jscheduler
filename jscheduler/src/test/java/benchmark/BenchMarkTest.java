package benchmark;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class BenchMarkTest {

    private static final int POOL_SIZE = 100;
    public static final int SCHEDULE_COUNT = 150000;
    private static final int WAIT_TIME_MILLI = 30000;

    @Test
    public void leftTest () {
        BenchMarkTestManager benchMarkTestManager = BenchMarkTestManager.getInstance();

        // LEFT
        benchMarkTestManager.startLeftExecutionTimer();
        ThreadPoolExecutorTest threadPoolExecutorTest = new ThreadPoolExecutorTest(POOL_SIZE);
        threadPoolExecutorTest.startOneShotForCount(SCHEDULE_COUNT);
        //

        benchMarkTestManager.sleepMilli(WAIT_TIME_MILLI);
        log.info("LEFT : Time={}, Count={}",
                benchMarkTestManager.getTotalLeftExecutionTimeMilli(),
                benchMarkTestManager.getLeftExecutionCount()
        );

        threadPoolExecutorTest.stop();
    }

    @Test
    public void rightTest () {
        BenchMarkTestManager benchMarkTestManager = BenchMarkTestManager.getInstance();

        // RIGHT
        benchMarkTestManager.startRightExecutionTimer();
        JSchedulerTest jSchedulerTest = new JSchedulerTest(POOL_SIZE);
        jSchedulerTest.startOneShotForCount(SCHEDULE_COUNT);
        //

        benchMarkTestManager.sleepMilli(WAIT_TIME_MILLI);
        log.info("RIGHT : Time={}, Count={}",
                benchMarkTestManager.getTotalRightExecutionTimeMilli(),
                benchMarkTestManager.getRightExecutionCount()
        );

        jSchedulerTest.stop();
    }

    @Test
    public void threadPoolExecutorTest() {
        ThreadPoolExecutorTest threadPoolExecutorTest = new ThreadPoolExecutorTest(POOL_SIZE);
        threadPoolExecutorTest.startAtFixedRate();

        BenchMarkTestManager.getInstance().sleepMilli(WAIT_TIME_MILLI);
        threadPoolExecutorTest.stop();
    }


}
