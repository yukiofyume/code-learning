package com.lwh.learning.esapi;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author lwh
 * @date 2025-01-18 22:14:39
 * @describe -
 */
class MainTest {

    private final Logger LOGGER = LoggerFactory.getLogger(MainTest.class);

    public ElasticsearchTransport getTransport() {
        // 0.身份验证
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "changeme"));

        // 1.创建低级客户端
        RestClient restClient = RestClient
                .builder(new HttpHost("localhost", 9200))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider))
                .build();

        // 2.创建传输映射器
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    public ElasticsearchClient getElasticsearchClient() {
        // 3.创建客户端
        return new ElasticsearchClient(getTransport());
    }

    /**
     * 1.es 低级客户端实例
     */
    @Test
    void firstTest() throws IOException {
        // 3.创建客户端
        ElasticsearchClient esClient = getElasticsearchClient();

        // 4.发送请求
        SearchResponse<Object> search = esClient.search(s -> s
                .index("alarm_info_2025")
                .query(q -> q
                        .term(t -> t
                                .field("id")
                                .value(v -> v.longValue(5663145)))

                ), Object.class);

        for (Hit<Object> hit : search.hits().hits()) {
            System.out.println(hit.source());
        }

        esClient.close();
    }

    /**
     * 2.创建索引
     */
    @Test
    void secondTest() throws IOException {
        ElasticsearchClient elasticsearchClient = getElasticsearchClient();
        elasticsearchClient.indices().create(c -> c.index("products"));
        elasticsearchClient.close();
    }

    /**
     * 3.非阻塞和异步客户端
     */
    @Test
    void thirdTest() throws IOException {
        ElasticsearchClient elasticsearchClient = getElasticsearchClient();


        TestEntity testEntity = new TestEntity();
        testEntity.setId("1");
        testEntity.setTitle("title1");
        testEntity.setContent("content1");
        testEntity.setTimestamp(System.currentTimeMillis());


        elasticsearchClient.index(IndexRequest.of(b -> b
                .index("products")
                .id("foo")
                .document(testEntity)));

        if (elasticsearchClient.exists(b -> b.index("products").id("foo")).value()) {
            LOGGER.info("product exists");
        }

        elasticsearchClient.close();
        // 异步非阻塞客户端
        ElasticsearchAsyncClient asyncClient = new ElasticsearchAsyncClient(getTransport());
        asyncClient
                .exists(b -> b.index("products").id("foo"))
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        LOGGER.error("fail to index", exception);
                    } else {
                        LOGGER.info("product exists");
                    }
                });
    }

    /**
     * 4.构建 api 对象
     */
    @Test
    void fourthTest() throws IOException {
        ElasticsearchClient esClient = getElasticsearchClient();
        CreateIndexResponse createIndexResponse = esClient.indices().create(
                new CreateIndexRequest.Builder()
                        .index("my-index")
                        .aliases("foo", new Alias.Builder().isWriteIndex(true).build())
                        .build()
        );

        // lambda表达式
        CreateIndexResponse foo = esClient.indices()
                .create(createIndexBuilder -> createIndexBuilder
                        .index("my-index")
                        .aliases("foo", aliasBuilder -> aliasBuilder.isWriteIndex(true))
                );
    }

    @Test
    void fifthTest() throws IOException {
        // 1.准备索引名称列表
        List<String> names = Arrays.asList("idx-a", "idx-b", "idx-c");

        // 2.为字段 “foo” 和 “bar” 准备基数聚合
        Map<String, Aggregation> cardinalities = new HashMap<>();
        cardinalities.put("foo-count", Aggregation.of(a -> a.cardinality(c -> c.field("foo"))));
        cardinalities.put("bar-count", Aggregation.of(a -> a.cardinality(c -> c.field("bar"))));

        // 3.准备一个聚合，用于计算 “size” 字段的平均值
        final Aggregation avgSize = Aggregation.of(a -> a.avg(v -> v.field("size")));

        SearchRequest searchRequest = SearchRequest.of(r -> r
                .index(names)
                .index("idx-d")
                .index("idx-e", "idx-f", "idx-g")
                .sort(s -> s.field(f -> f.field("foo").order(SortOrder.Asc)))
                .sort(s -> s.field(f -> f.field("bar").order(SortOrder.Desc)))
                .aggregations(cardinalities)
                .aggregations("avg-size", avgSize)
                .aggregations("price-histogram", a -> a.histogram(h -> h.field("price")))
        );
    }

    @Test
    void fifthTest2() throws IOException {
        String s = "pwwkew";
        System.out.println(lengthOfLongestSubstring(s));
    }

    public int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        char[] chars = s.toCharArray();
        int ret = 0;
        for (int l = 0, r = 0; r < chars.length; r++) {
            // 如果向右滑动窗口的时候发现了set中已经存在的元素
            System.out.println(chars[r]);
            System.out.println(l);
            System.out.println(r);
            while (set.contains(chars[r])) {
                set.remove(chars[l]);
                l++;
            }
            set.add(chars[r]);
            ret = Math.max(ret, r - l + 1);
        }
        return ret;
    }
}