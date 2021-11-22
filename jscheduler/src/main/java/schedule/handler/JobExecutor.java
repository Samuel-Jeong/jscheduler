package schedule.handler;

import job.base.Job;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import util.ConcurrentCyclicFIFO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class JobExecutor {

    private final int index;
    private final ConcurrentCyclicFIFO<Job> jobBuffer = new ConcurrentCyclicFIFO<>();
    private final ExecutorService executorService;
    private boolean isAlive = true;

    ////////////////////////////////////////////////////////////////////////////////

    public JobExecutor(int index) {
        this.index = index;

        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("[JobExecutor]" + index + "-[%d]")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();
        executorService = Executors.newFixedThreadPool(1, threadFactory);
        executorService.execute(this::run);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void run() {
        while(isAlive) {
            try {
                Job job = jobBuffer.poll();
                if (job == null) {
                    return;
                }

                executorService.execute(job);
            } catch (Exception e) {
                break;
            }
        }
    }

    public void stop() {
        isAlive = false;
        executorService.shutdown();
        jobBuffer.clear();
    }

    public void addJob(Job job) {
        jobBuffer.offer(job);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public int getIndex() {
        return index;
    }
}
