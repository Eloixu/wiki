package com.pwc.wiki.job;

import com.pwc.wiki.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Autowired
    DocService docService;

    /**
     * 固定时间间隔，fixedRate单位毫秒
     */
//    @Scheduled(fixedRate = 1000)
//    public void simple() throws InterruptedException {
//        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
//        String dateString = formatter.format(new Date());
//        Thread.sleep(2000);
//        LOG.info("每隔5秒钟执行一次： {}", dateString);
//    }

    /**
     * 每30秒更新电子书信息
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void cron(){
        LOG.info("更新电子书下的文档数据开始");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("更新电子书下的文档数据结束，耗时：{}毫秒", System.currentTimeMillis() - start);
    }

}
