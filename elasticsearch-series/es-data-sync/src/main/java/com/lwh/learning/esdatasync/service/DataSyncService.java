package com.lwh.learning.esdatasync.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.lwh.learning.esdatasync.elasticsearch.ElasticSearchUtils;
import com.lwh.learning.esdatasync.entity.AlarmInfo;
import com.lwh.learning.esdatasync.mapper.AlarmInfoMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author lwh
 * @date 2025-03-02 10:19:04
 * @describe -
 */
@Service
public class DataSyncService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final static int BATCH_SIZE = 1000;
    private final static String INDEX_NAME = "alarm_info";
    private final static int THREAD_COUNT = 100;

    @Autowired
    private AlarmInfoMapper alarmInfoMapper;

    public void importDataToEs() {
        long lastId = 699999;

        ExecutorService executor = new ThreadPoolExecutor(
                THREAD_COUNT, // 核心线程数
                THREAD_COUNT, // 最大线程数
                60L, // 空闲线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), // 任务队列大小
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：由调用线程执行任务
        );

        while (true) {
            List<AlarmInfo> alarmInfoList =
                    alarmInfoMapper.selectAll(lastId, BATCH_SIZE * THREAD_COUNT);
            if (CollectionUtils.isEmpty(alarmInfoList)) {
                LOGGER.info("All data deal");
                break;
            }
            lastId = alarmInfoList.getLast().getId();
            alarmInfoList.sort(Comparator.comparing(AlarmInfo::getId));
            LOGGER.info("Deal data id {} -> {}", alarmInfoList.getFirst().getId(), alarmInfoList.getLast().getId());

            for (int offset = 0; offset < alarmInfoList.size(); offset++) {
                int finalOffset = offset;
                executor.submit(() -> {
                    List<AlarmInfo> curList = alarmInfoList.stream()
                            .filter(item -> item.getId() >= (long) finalOffset * BATCH_SIZE && item.getId() <= (long) (finalOffset + 1) * BATCH_SIZE)
                            .toList();
                    if (CollectionUtils.isNotEmpty(curList)) {
                        bulkInsertToElasticsearch(curList);
                    }
                });
            }
        }

        executor.shutdown();
    }

    /**
     * bulk api 导入数据到 es
     *
     * @param alarmInfoList 数据
     */
    private void bulkInsertToElasticsearch(List<AlarmInfo> alarmInfoList) {
        List<BulkOperation> bulkOperations = new ArrayList<>();
        for (AlarmInfo alarmInfo : alarmInfoList) {
            bulkOperations.add(BulkOperation.of(op -> op.index(indexRequest -> indexRequest
                    .index(INDEX_NAME)
                    .id(alarmInfo.getId().toString())
                    .document(alarmInfo)
            )));
        }
        ElasticsearchClient elasticsearchClient = ElasticSearchUtils.elasticsearchClient;
        try {
            BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequest -> bulkRequest
                    .operations(bulkOperations)
            );
            LOGGER.info("Bulk operations success {}", bulkResponse.took());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
