package com.action.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Article {

    private Long id;

    private String title;

    private String context;

    private Long hits;
}
