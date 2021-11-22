package schedule.handler;

import job.base.Job;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class PriorityJobHandler extends JobHandler {

    private ExecutorService priorityJobPoolExecutor;
    private final PriorityBlockingQueue<Job> priorityQueue;

    public PriorityJobHandler(int poolSize, int queueSize) {
        super(poolSize, queueSize);

        priorityQueue = new PriorityBlockingQueue<>(
                queueSize,
                Comparator.comparing(Job::getPriority)
        );
    }

    public void init() {
        if (priorityJobPoolExecutor == null) {
            priorityJobPoolExecutor = Executors.newFixedThreadPool(poolSize);
            ExecutorService priorityJobScheduler = Executors.newSingleThreadExecutor();
            priorityJobScheduler.execute(() -> {
                while (true) {
                    try {
                        priorityJobPoolExecutor.execute(priorityQueue.take());
                    } catch (InterruptedException e) {
                        // exception needs special handling > ignore
                        break;
                    }
                }
            });
        }
    }

    @Override
    public boolean start(Job job) {
        if (job == null) {
            return false;
        }

        init();

        return priorityQueue.add(job);
    }

    @Override
    public boolean stop(Job job) {
        if (job == null) {
            return false;
        }

        return priorityQueue.remove(job);
    }

    @Override
    public void stopAll() {
        priorityQueue.clear();

        if (priorityJobPoolExecutor != null) {
            priorityJobPoolExecutor.shutdown();
            priorityJobPoolExecutor = null;
        }
    }

    @Override
    public String toString() {
        return "PriorityJobHandler{" +
                "poolSize=" + poolSize +
                ", queueTotalSize=" + dataStructureSize +
                ", priorityQueueSize=" + priorityQueue.size() +
                '}';
    }
}
