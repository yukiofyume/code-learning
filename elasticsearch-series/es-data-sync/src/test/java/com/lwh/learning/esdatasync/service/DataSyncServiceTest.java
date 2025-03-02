package com.lwh.learning.esdatasync.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author lwh
 * @date 2025-03-02 10:58:54
 * @describe -
 */
@SpringBootTest
class DataSyncServiceTest {

    @Autowired
    private DataSyncService dataSyncService;

    @Test
    void importDataToEsTest() {
        dataSyncService.importDataToEs();
    }
}