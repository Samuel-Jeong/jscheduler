package benchmark;

import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class BenchMarkTestManager {

    private static BenchMarkTestManager benchMarkTestManager = new BenchMarkTestManager();

    private BenchMarkTestManager() {}

    public static BenchMarkTestManager getInstance() {
        return benchMarkTestManager;
    }

    ///////////////////////////////////////

    private final AtomicLong leftExecutionCount = new AtomicLong(0);
    private final AtomicLong rightExecutionCount = new AtomicLong(0);

    public long getLeftExecutionCount() {
        return leftExecutionCount.get();
    }

    public void setLeftExecutionCount(long leftExecutionCount) {
        this.leftExecutionCount.set(leftExecutionCount);
    }

    public long incAndGetLeftExecutionCount() {
        return this.leftExecutionCount.incrementAndGet();
    }

    public long getRightExecutionCount() {
        return rightExecutionCount.get();
    }

    public void setRightExecutionCount(long rightExecutionCount) {
        this.rightExecutionCount.set(rightExecutionCount);
    }

    public long incAndGetRightExecutionCount() {
        return this.rightExecutionCount.incrementAndGet();
    }

    ///////////////////////////////////////

    private StopWatch leftExecutionTimer = null;
    private StopWatch rightExecutionTimer = null;

    private long totalLeftExecutionTime = 0;
    private long totalRightExecutionTime = 0;

    public void startLeftExecutionTimer() {
        if (leftExecutionTimer != null) { return; }
        leftExecutionTimer = new StopWatch();
        leftExecutionTimer.reset();
        leftExecutionTimer.start();
    }

    public void stopLeftExecutionTimer() {
        if (leftExecutionTimer == null) { return; }
        leftExecutionTimer.stop();
        totalLeftExecutionTime = leftExecutionTimer.getTime(TimeUnit.MILLISECONDS);
        leftExecutionTimer = null;
    }

    public long getTotalLeftExecutionTimeMilli() {
        return totalLeftExecutionTime;
    }

    public void startRightExecutionTimer() {
        if (rightExecutionTimer != null) { return; }
        rightExecutionTimer = new StopWatch();
        rightExecutionTimer.reset();
        rightExecutionTimer.start();
    }

    public void stopRightExecutionTimer() {
        if (rightExecutionTimer == null) { return; }
        rightExecutionTimer.stop();
        totalRightExecutionTime = rightExecutionTimer.getTime(TimeUnit.MILLISECONDS);
        rightExecutionTimer = null;
    }

    public long getTotalRightExecutionTimeMilli() {
        return totalRightExecutionTime;
    }

    ///////////////////////////////////////

    public void sleepMilli(long time) {
        try {
            TimeUnit timeUnit = TimeUnit.MILLISECONDS;
            timeUnit.sleep(time);
        } catch (Exception e) {
            // ignore
        }
    }

}
