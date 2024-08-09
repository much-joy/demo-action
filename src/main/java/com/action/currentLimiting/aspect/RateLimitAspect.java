package com.action.currentLimiting.aspect;

import com.action.annotation.RateLimit;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Aspect
public class RateLimitAspect {

    private final ConcurrentHashMap<String, RateLimiter> EXISTED_RATE_LIMITERS = new ConcurrentHashMap<>();

    /**
     * 切点，拦截所有是用了@RateLimit注解的方法
     */
    @Pointcut("@annotation(com.action.annotation.RateLimit)")
    public void rateLimit() {
    }

    @Around("rateLimit()")
    public Object around(ProceedingJoinPoint point) throws Throwable{
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimit annotation = AnnotationUtils.findAnnotation(method, RateLimit.class);
        RateLimiter rateLimiter = EXISTED_RATE_LIMITERS.computeIfAbsent(method.getName(), k -> RateLimiter.create(annotation.limit()));

        if (rateLimiter !=null && rateLimiter.tryAcquire()){
            return point.proceed();
        }else {
            throw new RuntimeException("too many requests, please try again later...");
        }

    }

}
