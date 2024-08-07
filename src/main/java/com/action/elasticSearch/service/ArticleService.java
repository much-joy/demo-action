package com.action.elasticSearch.service;

import com.action.entity.Article;

import java.io.IOException;


public interface ArticleService {

    /**
     * 保存文档
     * @param data
     * @return
     * @throws IOException
     */
    public String saveDocument( Article data) throws IOException;


    /**
     * 索引不存在就创建
     * @param indexName
     * @param indexMapping
     * @throws IOException
     */
    public void createIndexIfNotExists(String indexName,String indexMapping) throws IOException;

}
