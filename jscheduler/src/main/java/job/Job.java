package job;

import java.util.concurrent.TimeUnit;

public abstract class Job implements Runnable {

    private final String name;
    private final int interval;
    private final TimeUnit timeUnit; // ex) TimeUnit.MILLISECONDS

    public Job(String name, int interval, TimeUnit timeUnit) {
        this.name = name;
        this.interval = interval;
        this.timeUnit = timeUnit;
    }

    public String getName() {
        return name;
    }

    public int getInterval() {
        return interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

}
