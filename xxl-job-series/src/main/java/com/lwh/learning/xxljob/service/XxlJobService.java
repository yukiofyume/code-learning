package com.lwh.learning.xxljob.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author lwh
 * @date 2025-03-18 20:29:25
 * @describe -
 */
@Service
public class XxlJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XxlJobService.class);

    @XxlJob(value = "testJob")
    public void testJob() throws InterruptedException {
        XxlJobHelper.log("xxl-job testJob");

        for (int i = 0; i < 5; i++) {
            XxlJobHelper.log("beat at:{}", i);
            LOGGER.info("ok");
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
