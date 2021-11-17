package base;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ScheduleUnit {

    public static final int DEFAULT_THREAD_COUNT = 5;

    private final String key;
    private final long createdTime;
    private final int threadCount;
    private final long delay;

    ////////////////////////////////////////////////////////////////////////////////

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

    ////////////////////////////////////////////////////////////////////////////////

    public ScheduleUnit(String key, int threadCount, long delay) {
        this.key = key;
        this.createdTime = System.currentTimeMillis();
        this.threadCount = threadCount;
        this.delay = delay;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void start() {
        if (scheduledThreadPoolExecutor == null) {
            if (threadCount > 0) {
                scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(threadCount);
            } else {
                scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(DEFAULT_THREAD_COUNT);
            }
        }
    }

    public void stop() {
        if (scheduledThreadPoolExecutor != null) {
            scheduledThreadPoolExecutor.shutdown();
            scheduledThreadPoolExecutor = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String getKey() {
        return key;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public long getDelay() {
        return delay;
    }

    ////////////////////////////////////////////////////////////////////////////////

}
