package benchmark;

public interface SchedulerFactory {

    void startOneShotForCount(int count);

    void startAtFixedRate();

    void stop();

}
