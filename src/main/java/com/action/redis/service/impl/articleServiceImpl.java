package com.action.redis.service.impl;

import com.action.redis.service.articleService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

import java.util.*;

public class articleServiceImpl implements articleService {

    private static final int ONE_WEEK_IN_SECONDS = 7 * 86400;
    private static final int VOTE_SCORE = 432; //每获得一次投票，增加432分

    private static final int ARTICLES_PER_PAGE = 25; //每页文章数

    /**
     * redisTemplate.opsForValue(); // 操作字符串
     * redisTemplate.opsForHash(); // 操作hash
     * redisTemplate.opsForList(); // 操作list
     * redisTemplate.opsForSet(); // 操作set
     * redisTemplate.opsForZSet(); // 操作zset
     */

    /**
     * voted:文章以投票名单 key为voted+文章ID，value 为用户
     * score:记录文章的分数 key为score+文章Id，value 为分数
     * votes:文章投票数量
     * time:发布时间，根据时间排序的有序集合 计算投票截止时间
     */

    @Autowired
    private Jedis jedis;


    /**
     * @param userName
     * @param title
     * @param link
     * @return
     */
    @Override
    public String postArticle(String userName, String title, String link) {
        Long articleId = jedis.incr("article:");//自增，生成文章id（String列行）
        //文章点赞加入set集合
        String voted = "voted:" + articleId;
        jedis.sadd(voted, userName);//初始化投票，将文章用户加入到投票集合中
        jedis.expire(voted, ONE_WEEK_IN_SECONDS);//设置投票自动过期

        double now = System.currentTimeMillis() / 1000.0;

        //文章放入hash散列
        String articleKey = "article:" + articleId;

        //文章信息
        Map<String, String> article = new HashMap<>();
        article.put("title", title);
        article.put("link", link);
        article.put("poster", userName);
        article.put("time", Double.toString(now));
        article.put("votes", "1");

        jedis.hset(articleKey, article);

        //将文章加入到分数集合和时间集合中zset
        jedis.zadd("score:", now + VOTE_SCORE, articleKey);//评分会随着时间不断减少，排序慢慢靠后
        //文章加入的投票的时间
        jedis.zadd("time:", now, articleKey);


        return articleId.toString();
    }

    @Override
    public List<Map<String, String>> getArticle(int page,String key) {

        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE + 1;

        Set<String> articlekey = jedis.zrevrange(key, start, end);
        ArrayList<Map<String, String>> articles = new ArrayList<>();
        for (String id : articlekey) {
            Map<String, String> article = jedis.hgetAll(id);
            article.put("id", id);
            articles.add(article);
        }

        return articles;
    }

    /**
     * @param userName 投票用户
     * @param article  投票文章
     */
    @Override
    public void articleVote(String userName, String article) {
        double now = System.currentTimeMillis() / 1000.0;
        //投票截止时间
        double cutoff = now - ONE_WEEK_IN_SECONDS;
        Double zscore = jedis.zscore("time:", article);//投票时间
        if (zscore < cutoff) {
            //投票结束
            return;
        }

        //获取文章id
        String articleId = article.substring(article.indexOf(":") + 1);

        //如果没有参与过投票，开始参与
        String voted = "voted:" + articleId;
        if (jedis.sadd(voted, userName) == 1) {
            //参与之后，增加分数和计数
            jedis.zincrby("score:", VOTE_SCORE, article);
            jedis.hincrBy(article, "votes", 1);//递增哈希字段的值
        }


    }

    @Override
    public void AddRemoveGroups(String articleId, List<String> toGroups, List<String> removeGroups) {
        String articlekey = "article:" + articleId;

        for (String group : toGroups) {
            jedis.sadd("group:" + group, articlekey);
        }
        for (String group : removeGroups) {
            jedis.srem("group:" + group, articlekey);
        }

    }

    @Override
    public List<Map<String, String>> getGroupArticles(String group, int page, String order) {
        String key = order + group;
        if (!jedis.exists(key)){
            jedis.zinterstore(key,new ZParams().aggregate(ZParams.Aggregate.MAX),"group:" + group,order);
            jedis.expire(key,60);
        }

        return getArticle(page,key);
    }
}
