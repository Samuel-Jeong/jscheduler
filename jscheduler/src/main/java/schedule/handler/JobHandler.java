package schedule.handler;

import job.base.Job;

public abstract class JobHandler {

    final int poolSize;
    final int dataStructureSize;

    public JobHandler(int poolSize, int dataStructureSize) {
        this.poolSize = poolSize;
        this.dataStructureSize = dataStructureSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public int getDataStructureSize() {
        return dataStructureSize;
    }

    public boolean start(Job job) {
        return true;
    }

    public boolean stop(Job job) {
        return true;
    }

    public void stopAll() {

    }

}
