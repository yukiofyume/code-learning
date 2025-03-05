package com.lwh.learning.esdatasync.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author lwh
 * @date 2025-02-26 21:05:16
 * @describe -
 */
public class ElasticSearchUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(ElasticSearchUtils.class);

    public final static ElasticsearchClient elasticsearchClient;

    public final static ElasticsearchAsyncClient elasticsearchAsyncClient;

    static {
        // json 映射
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        JsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

        // 0.身份验证
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "changeme"));
        try {
            HttpHost httpHost = new HttpHost("localhost", 9200);

            // 配置连接池
            PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());
            connectionManager.setMaxTotal(200); //最大连接数
            connectionManager.setDefaultMaxPerRoute(50); // 每个路由最大连接数


            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000) // 连接超时5000ms
                    .setSocketTimeout(60000) // 请求超时60000ms
                    .build();


            // 1.创建低级客户端
            RestClient restClient = RestClient
                    .builder(httpHost)
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider)
                            .setConnectionManager(connectionManager)
                            .setDefaultRequestConfig(requestConfig))
                    .build();

            // 2.创建传输映射器
            ElasticsearchTransport elasticsearchTransport = new RestClientTransport(restClient, jsonpMapper);
            elasticsearchClient = new ElasticsearchClient(elasticsearchTransport);
            elasticsearchAsyncClient = new ElasticsearchAsyncClient(elasticsearchTransport);
        } catch (IOReactorException e) {
            throw new RuntimeException(e);
        }
    }

    public static IndexResponse insertData(String index, String id, Object document) {
        try {
            return elasticsearchClient.index(IndexRequest.of(b -> b
                    .index(index)
                    .id(id)
                    .document(document)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 小数据量（百万级以下）： 1-3 个主分片，1 个副本。
     * 中等数据量（千万级）： 3-5 个主分片，1-2 个副本。
     * 大数据量（亿级）： 5-10 个主分片，1-2 个副本。
     *
     * @param index    索引名称
     * @param alias    别名
     * @param shards   分片数，默认为1
     * @param replicas 副本数，默认为1
     */
    public static CreateIndexResponse createIndex(String index, String alias, String shards, String replicas) {

        try {
            return elasticsearchClient.indices().create(c -> c
                    .index(index)
                    .settings(
                            s -> s
                                    .numberOfShards(StringUtils.isNotBlank(shards) ? shards : "1") // 设置主分片数
                                    .numberOfReplicas(StringUtils.isNotBlank(replicas) ? replicas : "1") // 设置副本数
                    )
                    .aliases(alias, new Alias.Builder().isWriteIndex(true).build())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
