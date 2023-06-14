package com.system.karyawan.controllers.scheduller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@EnableScheduling
public class SchedullerJob {
    private Logger logger = LoggerFactory.getLogger(SchedullerJob.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Scheduled(cron = "0 0/1 * * * *")
    public void doSchedule() throws Exception{
        logger.info("***********************************SCHEDULLER START "+dateFormat.format(new Date()));
    }
}
