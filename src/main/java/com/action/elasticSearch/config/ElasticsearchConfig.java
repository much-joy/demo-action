package com.action.elasticSearch.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchConfig {



    private String prefix;
    private String suffix;
    private String baseName;
    private List<String> uris;

    /**
     *在使用 @ConfigurationProperties 时，属性值的注入发生在 Spring 的生命周期管理过程中，
     * 而这个过程是在 Spring 创建并初始化 Bean 后才会进行。如果你在构造函数中尝试使用这些属性，
     * 可能会发生属性尚未注入的情况。这是因为构造函数注入发生在 Spring 完成属性注入之前，
     * 所以无法获取到通过 @ConfigurationProperties 注入的配置值。
     *
     *     public ElasticsearchConfig() {
     *         // 从配置文件的 uris 列表中解析出 HttpHost 对象
     *         HttpHost[] hosts = uris.stream()
     *                 .map(uri -> {
     *                     String[] hostPort = uri.split(":");
     *                     String host = hostPort[0];
     *                     int port = (hostPort.length > 1) ? Integer.parseInt(hostPort[1]) : 9200;  // 默认端口 9200
     *                     return new HttpHost(host, port, "http");
     *                 })
     *                 .toArray(HttpHost[]::new);
     *
     *         // 初始化 RestHighLevelClient
     *         this.client = new RestHighLevelClient(RestClient.builder(hosts));
     *     }
     *
     *
     * 如果非要使用构造函数注入，需要增加一个配置属性类
     * @Data
     * @Component
     * @ConfigurationProperties(prefix = "elasticsearch")
     * public class ElasticsearchProperties {
     *
     *     private String prefix;
     *     private String suffix;
     *     private List<String> uris;
     * }
     *
     *
     */



    //构造函数会在 Spring 容器创建该类的实例时执行
    // 构造函数使用配置文件中的 uris 初始化 RestHighLevelClient
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 从配置文件的 uris 列表中解析出 HttpHost 对象
        HttpHost[] hosts = uris.stream()
                .map(uri -> {
                    String[] hostPort = uri.replace("http://", "").split(":");
                    return new HttpHost(hostPort[0], Integer.parseInt(hostPort[1]), "http");
                })
                .toArray(HttpHost[]::new);

        // 初始化 RestHighLevelClient
        return new RestHighLevelClient(RestClient.builder(hosts));
    }




    /**
     close 方法会在 Spring 容器关闭或应用程序停止时自动执行。这是通过 @PreDestroy 注解实现的，该注解用于标记在 Spring Bean 生命周期结束时（即 Bean 被销毁之前）需要执行的方法
     */
    @PreDestroy
    public void close() throws  IOException {
        if(restHighLevelClient() != null){
            restHighLevelClient().close();
        }
    }


    // 获取完整的 indexName
    public String getFullIndexName() {
        return prefix + baseName + suffix;
    }

}
