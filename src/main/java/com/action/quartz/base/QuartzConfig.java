package com.action.quartz.base;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

//    @Bean("helloJob")
//    public JobDetail helloJobDetail() {
//        return JobBuilder.newJob(HelloJob.class)
//                .withIdentity("DateTimeJob")
//                .usingJobData("msg", "Hello Quartz")
//                .storeDurably()//即使没有Trigger关联时，也不需要删除该JobDetail
//                .build();
//    }
//
//
//    @Bean
//    public Trigger printTimeJobTrigger() {
//        // 每秒执行一次
//        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0/1 * * * * ?");
//        return TriggerBuilder.newTrigger()
//                .forJob(helloJobDetail())
//                .withIdentity("quartzTaskService")
//                .withSchedule(cronScheduleBuilder)
//                .build();
//    }

}
