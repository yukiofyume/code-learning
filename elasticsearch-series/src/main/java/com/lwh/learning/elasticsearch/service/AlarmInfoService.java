package com.lwh.learning.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.lwh.learning.elasticsearch.entity.AlarmInfo;
import com.lwh.learning.elasticsearch.mapper.AlarmInfoMapper;
import com.lwh.learning.elasticsearch.req.AlarmInfoReq;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lwh
 * @date 2025-01-14 21:12:26
 * @describe -
 */
@Service
public class AlarmInfoService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AlarmInfoMapper alarmInfoMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public void searAlarms(AlarmInfoReq req) throws IOException {
        List<Query> mustQueryList = new ArrayList<>();
        if (Objects.nonNull(req.getStartAlarmTime()) && Objects.nonNull(req.getEndAlarmTime())) {
            mustQueryList.add(
                    Query.of(q -> q.range(
                            RangeQuery.of(r -> r.field("alarmTime")
                                    .gte(JsonData.of(req.getStartAlarmTime().toInstant(ZoneOffset.of("+8")).toEpochMilli()))
                                    .lte(JsonData.of(req.getEndAlarmTime().toInstant(ZoneOffset.of("+8")).toEpochMilli())))
                    ))
            );
            Query query = Query.of(q -> q.bool(b -> b.must(mustQueryList)));
            SearchRequest searchRequest = SearchRequest.of(sr -> sr
                    .index("alarm_info_2025")
                    .query(query)
                    .from(1)
                    .size(100));
            SearchResponse<AlarmInfo> search = elasticsearchClient.search(searchRequest, AlarmInfo.class);
            List<Hit<AlarmInfo>> hits = search.hits().hits();
            List<AlarmInfo> list = hits.stream()
                    .map(Hit::source)
                    .toList();
//            JsonData jsonData = list.get(0);
//            String string = jsonData.toString();
//            JsonValue json = jsonData.toJson();
            int i = 0;
        }

//        QueryBuilders.bool()
//                .must(s -> {
//                    if (Objects.nonNull(req.getStartAlarmTime()) && Objects.nonNull(req.getEndAlarmTime())) {
//                       return QueryBuilders.range(at-> at.gte(JsonData.of(req.getStartAlarmTime().toString())).lte(JsonData.of(req.getEndAlarmTime())));
//                    } else {
//                        return QueryBuilders.range().build().;
//                    }
//                });
    }

    @Transactional(rollbackFor = Exception.class)
    public void insertAlarmInfo(AlarmInfo alarmInfo) {
        // 1. 插入 PostgreSQL
        alarmInfoMapper.insertSelective(alarmInfo);

        // 2. 异步发送到 RabbitMQ
        rabbitTemplate.convertAndSend("alarm.exchange", "alarm.routing.key", alarmInfo);
    }

    /**
     * 批量将数据同步到es中
     */
//    @Transactional(rollbackFor = Exception.class)
    public void batchInsertAlarmInfo(List<AlarmInfo> alarmInfos) {
        LOGGER.info("need send to  size:{}", alarmInfos.size());
        // Create a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        try {
            // Divide the data into smaller chunks for each thread
            List<List<AlarmInfo>> partitions = partition(alarmInfos, 10000);

            // Create a list to hold CompletableFutures
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (List<AlarmInfo> partition : partitions) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
                        AlarmInfoMapper mapper = sqlSession.getMapper(AlarmInfoMapper.class);
                        for (AlarmInfo alarmInfo : partition) {
                            mapper.insertSelective(alarmInfo);
                        }
                        sqlSession.commit();
                    }
                }, executorService);

                futures.add(future);
            }

            // Wait for all threads to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        } finally {
            // Shut down the thread pool
            executorService.shutdown();
        }
//        LOGGER.info("need send to es size:{}", alarmInfos.size());
//        int year = alarmInfos.get(0).getAlarmTime().getYear();
//        elasticsearchTemplate.save(alarmInfos, IndexCoordinates.of("alarm_info" + "_" + year));
    }

    private List<List<AlarmInfo>> partition(List<AlarmInfo> list, int size) {
        List<List<AlarmInfo>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
}
