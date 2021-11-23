package schedule.handler;

import job.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.RedBlackTree;

import java.util.Comparator;
import java.util.concurrent.*;

public class JobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    ////////////////////////////////////////////////////////////////////////////////

    private final String ownerName;
    private final int poolSize;
    private final ExecutorService priorityJobSelector;
    private final JobExecutor[] jobExecutors; // Round-Robin executor selection
    private int curExecutorIndex = 0;
    private boolean isAlive = true;

    private final RedBlackTree redBlackTree = new RedBlackTree();
    private final PriorityBlockingQueue<Job> priorityQueue;

    ////////////////////////////////////////////////////////////////////////////////

    public JobScheduler(String ownerName, int poolSize, int queueSize) {
        this.ownerName = ownerName;
        this.poolSize = poolSize;

        jobExecutors = new JobExecutor[poolSize];
        for (int i = 0; i < poolSize; i++) {
            jobExecutors[i] = new JobExecutor(i);
        }

        priorityQueue = new PriorityBlockingQueue<>(
                queueSize,
                Comparator.comparing(Job::getPriority)
        );

        priorityJobSelector = Executors.newSingleThreadExecutor();
        priorityJobSelector.execute(this::run);
    }

    private void run() {
        while (isAlive) {
            try {
                // dequeue 후 객체 null 여부에 상관없이 기다리지 않음
                // <-> take(): dequeue 후 객체가 null 이 아닐 때까지 기다림
                Job job = priorityQueue.poll();
                if (job == null) {
                    continue;
                }

                if (job.getIsFinished()) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("[{}({})]-[{}] is finished.", ownerName, curExecutorIndex, job.getName());
                    }
                    continue;
                }

                if (!job.isLasted() && job.decCurRemainRunCount() <= 0) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("[{}({})]-[{}] is finished.", curExecutorIndex, ownerName, job.getName());
                    }
                    continue;
                }

                jobExecutors[curExecutorIndex].addJob(job);
                if (logger.isTraceEnabled()) {
                    logger.trace("[{}({})]-[{}]: is running. ({})", ownerName, curExecutorIndex, job.getName(), job);
                }

                int interval = job.getInterval();
                if (interval > 0) {
                    new Thread(new FutureScheduler(interval, job)).start();
                    if (logger.isTraceEnabled()) {
                        logger.trace("[{}({})]-[{}] is scheduled.", ownerName, curExecutorIndex, job.getName());
                    }
                }

                curExecutorIndex++;
                if (curExecutorIndex >= poolSize) {
                    curExecutorIndex = 0;
                }
            } catch (Exception e) {
                break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean schedule(Job job) {
        int initialDelay = job.getInitialDelay();
        if (initialDelay > 0) {
            new Thread(new FutureScheduler(initialDelay, job)).start();
            return true;
        }

        return priorityQueue.offer(job);
    }

    public void stop(Job job) {
        job.setIsFinished(true);
    }

    public void stopAll() {
        isAlive = false;
        priorityQueue.clear();
        priorityJobSelector.shutdown();

        for (int i = 0; i < poolSize; i++) {
            jobExecutors[i].stop();
        }

        logger.trace("[{}({})] is finished.", ownerName, curExecutorIndex);
    }

    ////////////////////////////////////////////////////////////////////////////////

    private class FutureScheduler implements Runnable {

        private final long sleepTime;
        private final TimeUnit timeUnit;

        private final Job job;

        ////////////////////////////////////////////////////////////////////////////////

        public FutureScheduler(long sleepTime, Job job) {
            this.sleepTime = sleepTime;
            this.timeUnit = job.getTimeUnit();
            this.job = job;
        }

        ////////////////////////////////////////////////////////////////////////////////

        @Override
        public void run() {
            try {
                timeUnit.sleep(sleepTime);

                priorityQueue.offer(job);
            } catch (Exception e) {
                job.setIsFinished(true);
            }
        }

    }

}
