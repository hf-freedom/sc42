package com.library.scheduler;

import com.library.service.FineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LibraryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(LibraryScheduler.class);

    @Autowired
    private FineService fineService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDueReminder() {
        logger.info("执行到期提醒任务");
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void processOverdueRecords() {
        logger.info("执行逾期罚金生成任务");
        fineService.processOverdueRecords();
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void checkOverdueFinesForBlacklist() {
        logger.info("执行黑名单检查任务");
        fineService.checkOverdueFinesForBlacklist();
    }
}