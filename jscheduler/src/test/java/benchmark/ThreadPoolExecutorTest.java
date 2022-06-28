package benchmark;

import example.SumHandlerRunnable;
import org.junit.Test;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorTest implements SchedulerFactory {

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    public ThreadPoolExecutorTest(int poolSize) {
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(poolSize);
    }


    @Override
    public void startOneShotForCount(int count) {
        for (int i = 0; i < count; i++) {
            scheduledThreadPoolExecutor.schedule(
                    new SumHandlerRunnable(scheduledThreadPoolExecutor, true, 100000),
                    0,
                    TimeUnit.SECONDS
            );
        }
    }

    @Override
    public void startAtFixedRate() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(
                new SumHandlerRunnable(scheduledThreadPoolExecutor, true, 100),
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void stop() {
        scheduledThreadPoolExecutor.shutdown();
    }

}
