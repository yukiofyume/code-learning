package com.lwh.learning.esdatasync.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.lwh.learning.common.util.JacksonUtils;
import com.lwh.learning.esdatasync.entity.AlarmInfo;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lwh
 * @date 2025-03-01 15:01:43
 * @describe -
 */

class ElasticSearchUtilsTest {

    private final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchUtilsTest.class);

    /**
     * 创建索引
     */
    @Test
    void createIndexTest() {
        CreateIndexResponse index = ElasticSearchUtils.createIndex("alarm_info_2025", "alarm_info", "3", "2");
        System.out.println(index.acknowledged());
    }

    @Test
    void insertTest() {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setId(1L);
        alarmInfo.setAlarmTime(LocalDateTime.now());
        alarmInfo.setHandleTime(LocalDateTime.now());
        alarmInfo.setHandleStatus(1);
        alarmInfo.setMessage("这是一条测试数据1");
        alarmInfo.setCreateTime(LocalDateTime.now());
        alarmInfo.setUpdateTime(LocalDateTime.now());
        IndexResponse response = ElasticSearchUtils.insertData("alarm_info_2025", String.valueOf(alarmInfo.getId()), alarmInfo);
        System.out.println(response.version());
    }

    /**
     * 插入数据测试
     */
    @Test
    void insertTestA() throws IOException {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setId(1L);
        alarmInfo.setAlarmTime(LocalDateTime.now());
        alarmInfo.setHandleTime(LocalDateTime.now());
        alarmInfo.setHandleStatus(1);
        alarmInfo.setMessage("这是一条测试数据231312312");
        alarmInfo.setCreateTime(LocalDateTime.now());
        alarmInfo.setUpdateTime(LocalDateTime.now());
        IndexResponse response = ElasticSearchUtils.elasticsearchClient.index(i -> i
                .index("alarm_info_new")
                .id(alarmInfo.getId().toString())
                .document(alarmInfo)
        );
        LOGGER.info("Indexed with version {}", response.version());
    }

    /**
     * IndexRequest.of() 方法
     */
    @Test
    void insertTestB() throws IOException {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setId(1L);
        alarmInfo.setAlarmTime(LocalDateTime.now());
        alarmInfo.setHandleTime(LocalDateTime.now());
        alarmInfo.setHandleStatus(1);
        alarmInfo.setMessage("这是一条测试数据231312312");
        alarmInfo.setCreateTime(LocalDateTime.now());
        alarmInfo.setUpdateTime(LocalDateTime.now());

        IndexRequest<AlarmInfo> indexRequest = IndexRequest.of(i -> i
                .index("alarm_info_new")
                .id(alarmInfo.getId().toString())
                .document(alarmInfo)
        );

        IndexResponse response = ElasticSearchUtils.elasticsearchClient.index(indexRequest);
        LOGGER.info("Indexed with version {}", response.version());
    }

    /**
     * 创建的建造器模式
     */
    @Test
    void insertTestC() throws IOException {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setId(1L);
        alarmInfo.setAlarmTime(LocalDateTime.now());
        alarmInfo.setHandleTime(LocalDateTime.now());
        alarmInfo.setHandleStatus(1);
        alarmInfo.setMessage("这是一条测试数据231312312");
        alarmInfo.setCreateTime(LocalDateTime.now());
        alarmInfo.setUpdateTime(LocalDateTime.now());

        IndexRequest<AlarmInfo> indexRequest = new IndexRequest.Builder<AlarmInfo>()
                .index("alarm_info_new")
                .id(alarmInfo.getId().toString())
                .document(alarmInfo)
                .build();
        IndexResponse response = ElasticSearchUtils.elasticsearchClient.index(indexRequest);
        LOGGER.info("Indexed with version {}", response.version());
    }

    /**
     * 异步客户端
     */
    @Test
    void insertTestD() throws IOException {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setId(1L);
        alarmInfo.setAlarmTime(LocalDateTime.now());
        alarmInfo.setHandleTime(LocalDateTime.now());
        alarmInfo.setHandleStatus(1);
        alarmInfo.setMessage("这是一条测试数据231312312");
        alarmInfo.setCreateTime(LocalDateTime.now());
        alarmInfo.setUpdateTime(LocalDateTime.now());

        ElasticSearchUtils.elasticsearchAsyncClient.index(i -> i
                .index("alarm_info_new")
                .id(alarmInfo.getId().toString())
                .document(alarmInfo)
        ).whenComplete((response, e) -> {
            if (e != null) {
                LOGGER.error("Failed to index", e);
            } else {
                LOGGER.info("Indexed with version {}", response.version());
            }
        });
    }

    /**
     * get 获取元素
     */
    @Test
    void readDomainTest() throws IOException {
        ElasticsearchClient elasticsearchClient = ElasticSearchUtils.elasticsearchClient;
        GetResponse<AlarmInfo> response = elasticsearchClient.get(g -> g
                        .index("alarm_info_new")
                        .id("1")
                , AlarmInfo.class
        );
        if (response.found()) {
            AlarmInfo alarmInfo = response.source();
            LOGGER.info("Indexed with id {}", alarmInfo.getId());
        }
        {
            LOGGER.info("Product not found");
        }
    }

    /**
     * get response with json
     */
    @Test
    void readDomainTestA() throws IOException {
        ElasticsearchClient elasticsearchClient = ElasticSearchUtils.elasticsearchClient;

        GetResponse<JsonNode> response = elasticsearchClient.get(g -> g
                        .index("alarm_info_new")
                        .id("1"),
                JsonNode.class
        );
        if (response.found()) {
            JsonNode source = response.source();
            long id = source.get("id").asLong();
            LOGGER.info("Indexed with id {}", id);
        } else {
            LOGGER.info("Product not found");
        }
    }

    /**
     * 简单查询
     */
    @Test
    void searchTest() throws IOException {
        ElasticsearchClient elasticsearchClient = ElasticSearchUtils.elasticsearchClient;
        String messageText = "1";
        SearchResponse<AlarmInfo> response = elasticsearchClient.search(s -> s
                        .index("alarm_info_new")
                        .query(q -> q
                                .match(t -> t
                                        .field("id")
                                        .query(messageText)
                                )
                        )
                , AlarmInfo.class);
        TotalHits total = response.hits().total();
        boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

        if (isExactResult) {
            LOGGER.info("There are {} result", total.value());
        } else {
            LOGGER.info("There are more than {} result", total.value());
        }
        List<Hit<AlarmInfo>> hits = response.hits().hits();
        hits.forEach(s -> LOGGER.info("Element:{}", JacksonUtils.toJson(s.score())));
    }
}