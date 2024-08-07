package com.action.elasticSearch.service.impl;


import com.action.elasticSearch.config.ElasticsearchConfig;
import com.action.entity.Article;
import com.action.elasticSearch.service.ArticleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ElasticsearchConfig elasticsearchConfig;

    @Override
    public String saveDocument( Article data) throws IOException {
        createIndexIfNotExists(elasticsearchConfig.getFullIndexName(),null);
        ObjectMapper objectMapper = new ObjectMapper();
        String dataJson = objectMapper.writeValueAsString(data);
        IndexRequest request = new IndexRequest(elasticsearchConfig.getFullIndexName()).id(data.getId().toString()).source(dataJson, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.getId();
    }


    @Override
    public void createIndexIfNotExists(String indexName, String indexMapping) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        boolean exists = client.indices().exists(getIndexRequest,RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            createIndexRequest.settings(Settings.builder()
                    .put("index.number_of_shards", 1)  // 通常设置为 1 就足够了
                    .put("index.number_of_replicas", 0) // 单机环境建议设置为 0
            );
            if (!StringUtils.isEmpty(indexMapping)) {
                createIndexRequest.mapping(indexMapping,XContentType.JSON);
            }
             client.indices().create(createIndexRequest,RequestOptions.DEFAULT);
        }
    }


}
