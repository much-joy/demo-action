package com.action.redis.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public interface articleService {


    //上传文章，返回文章id
    String postArticle(String userName,String title, String link);

    //根据key + 页码获取文章
    List<Map<String,String>> getArticle(int page,String key);

    //点赞
    void articleVote(String userName,String article);

    //文章加入分组，或者从分组移除
    void AddRemoveGroups(String articleId,List<String> toGroups,List<String> removeGroups);

    //从分组中获取文章
    List<Map<String, String>> getGroupArticles(String group,int page,String order);


}
