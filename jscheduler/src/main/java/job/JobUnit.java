package job;

import job.base.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schedule.handler.JobHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class JobUnit {

    private static final Logger logger = LoggerFactory.getLogger(JobUnit.class);

    private final String name;
    private final long createdTime;

    private final JobHandler jobHandler;

    ////////////////////////////////////////////////////////////////////////////////

    public JobUnit(String name, JobHandler jobHandler) {
        this.name = name;
        this.createdTime = System.currentTimeMillis();
        this.jobHandler = jobHandler;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean start(Job job) {
        return jobHandler.start(job);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean stop(Job job) {
        return jobHandler.stop(job);
    }

    public void stopAll() {
        jobHandler.stopAll();
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return "JobUnit{" +
                "name='" + name + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
