package com.action.elasticSearch.controller;

import com.action.entity.Article;
import com.action.elasticSearch.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PutMapping("/createIndex")
    public void createIndex(@RequestBody Article data) throws IOException {
        articleService.saveDocument(data);
    }
}
