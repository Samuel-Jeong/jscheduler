import base.ScheduleUnit;
import module.ScheduleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSchedulerMain {

    private static final Logger logger = LoggerFactory.getLogger(JSchedulerMain.class);

    public static void main(String[] args) {
        // TODO TEST

        ScheduleManager scheduleManager = ScheduleManager.getInstance();
        ScheduleUnit sendRtp0ScheduleUnit = scheduleManager.addScheduleUnit(
                "THREAD_0_SEND_RTP",
                10,
                1000
        );
        logger.debug("sendRtp0ScheduleUnit: {}", sendRtp0ScheduleUnit);
    }

}
