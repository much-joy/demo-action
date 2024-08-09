package com.action.currentLimiting.controller;

import com.action.annotation.RateLimit;
import com.action.response.ResponseResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@RestController
@RequestMapping("/v1/limit")
public class RateLimitTestController {

    @RateLimit(limit = 5)
    @GetMapping("/get")
    public ResponseResult<String> limit (){
        log.info("limit start");
        return ResponseResult.success();
    }


}
