package com.action.quartz.cluster.manager;

import com.action.quartz.cluster.entity.JobDetails;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Job管理类
 */
@Component
public class QuartzManager {

    @Autowired
    private Scheduler sched;

    /**
     * 创建or更新任务，存在则更新不存在创建
     *
     * @param jobClass     任务类
     * @param jobName      任务名称
     * @param jobGroupName 任务组名称
     * @param jobCron      cron表达式
     */
    public void addOrUpdateJob(Class<? extends QuartzJobBean> jobClass, String jobName, String jobGroupName, String jobCron) {

        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                addJob(jobClass, jobName, jobGroupName, jobCron);
            } else {
                if (trigger.getCronExpression().equals(jobCron)) {
                    return;
                }
                updateJob(jobName,jobGroupName,jobCron);

            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }


    /**
     * 增加一个job
     * @param jobClass      任务实现类
     * @param jobName       任务名称
     * @param jobGroupName  任务组名
     * @param jobCron       cron表达式
     */
    public void addJob(Class<? extends QuartzJobBean> jobClass,String jobName,String jobGroupName,String jobCron){

        try{
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
//                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))//指定任务将在当前时间的 1 秒后开始执行。
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobCron))//根据传入的 jobCron 表达式定义调度规则
                    .startNow() // 设置触发器立即生效
                    .build();   //构建触发器

            sched.scheduleJob(jobDetail,trigger);
            if (!sched.isShutdown()) {
                sched.start();
            }

        }catch (SchedulerException e){
            e.printStackTrace();
        }

    }

    public void addJob(Class<? extends QuartzJobBean> jobClass,String jobName,String jobGroupName,String jobCron,int jobTime){
        addJob(jobClass,jobName,jobGroupName,jobCron,jobTime,-1);
    }

    public void addJob(Class<? extends QuartzJobBean> jobClass,String jobName,String jobGroupName,String jobCron,int jobTime,int jobTimes){

        try {
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();
            Trigger trigger;
            if (jobTime < 0 ) {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobName, jobGroupName)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(jobTime)  // 每隔 jobTime 秒执行一次
                                .repeatForever())  // 无限次重复执行
                        .startNow()
                        .build();
            }else {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobName, jobGroupName)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(jobTime)  // 每隔 jobTime 秒执行一次
                                .withRepeatCount(jobTimes))  // 设置任务重复的次数
                        .startNow()
                        .build();
            }
            sched.scheduleJob(jobDetail,trigger);
            if (!sched.isShutdown()) {
                sched.start();
            }
        }catch (SchedulerException e){
            e.printStackTrace();
        }

    }


    /**
     * 更新一个job
     * @param jobName
     * @param jobGroupJob
     * @param jobTime
     */
    public void updateJob(String jobName,String jobGroupJob,String jobTime){
        try{
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupJob);
            CronTrigger cronTrigger = (CronTrigger) sched.getTrigger(triggerKey);
            cronTrigger.getTriggerBuilder().withIdentity(triggerKey);
            sched.rescheduleJob(triggerKey,cronTrigger);//重启触发器
        }catch (SchedulerException e){
            e.printStackTrace();
        }
    }


    /**
     * 删除一个job
     * @param jobName
     * @param jobGroupName
     */
    public void deleteJob(String jobName,String jobGroupName){

        try{
            sched.pauseTrigger(TriggerKey.triggerKey(jobName,jobGroupName));//暂停关联的触发器
            sched.unscheduleJob(TriggerKey.triggerKey(jobName,jobGroupName));//取消调度相关的触发器
            sched.deleteJob(new JobKey(jobName, jobGroupName));//删除任务
        }catch (SchedulerException e){
            e.printStackTrace();
        }
    }


    /**
     * 暂停一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    public void pauseJob(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            sched.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    public void resumeJob(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            sched.resumeJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    /**
     * 立即执行一个job
     *
     * @param jobName
     * @param jobGroupName
     */
    public void runAJobNow(String jobName, String jobGroupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            sched.triggerJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public PageInfo<JobDetails> queryAllJobBean(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<JobDetails> jobList = null;
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = sched.getJobKeys(matcher);
            jobList = new ArrayList<>();
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = sched.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    JobDetails jobDetails = new JobDetails();
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        jobDetails.setCronExpression(cronTrigger.getCronExpression());
                        jobDetails.setTimeZone(cronTrigger.getTimeZone().getDisplayName());
                    }
                    jobDetails.setTriggerGroupName(trigger.getKey().getName());
                    jobDetails.setTriggerName(trigger.getKey().getGroup());
                    jobDetails.setJobGroupName(jobKey.getGroup());
                    jobDetails.setJobName(jobKey.getName());
                    jobDetails.setStartTime(trigger.getStartTime());
                    jobDetails.setJobClassName(sched.getJobDetail(jobKey).getJobClass().getName());
                    jobDetails.setNextFireTime(trigger.getNextFireTime());
                    jobDetails.setPreviousFireTime(trigger.getPreviousFireTime());
                    jobDetails.setStatus(sched.getTriggerState(trigger.getKey()).name());
                    jobList.add(jobDetails);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return new PageInfo<>(jobList);
    }

    /**
     * 获取所有计划中的任务列表
     *
     * @return
     */
    public List<Map<String, Object>> queryAllJob() {
        List<Map<String, Object>> jobList = null;
        try {
            GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
            Set<JobKey> jobKeys = sched.getJobKeys(matcher);
            jobList = new ArrayList<>();
            for (JobKey jobKey : jobKeys) {
                List<? extends Trigger> triggers = sched.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("jobName", jobKey.getName());
                    map.put("jobGroupName", jobKey.getGroup());
                    map.put("description", "trigger:" + trigger.getKey());
                    Trigger.TriggerState triggerState = sched.getTriggerState(trigger.getKey());
                    map.put("jobStatus", triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        map.put("jobTime", cronExpression);
                    }
                    jobList.add(map);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobList;
    }

    /**
     * 获取所有正在运行的job
     *
     * @return
     */
    public List<Map<String, Object>> queryRunJon() {
        List<Map<String, Object>> jobList = null;
        try {
            List<JobExecutionContext> executingJobs = sched.getCurrentlyExecutingJobs();
            jobList = new ArrayList<>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                Map<String, Object> map = new HashMap<>();
                JobDetail jobDetail = executingJob.getJobDetail();
                JobKey jobKey = jobDetail.getKey();
                Trigger trigger = executingJob.getTrigger();
                map.put("jobName", jobKey.getName());
                map.put("jobGroupName", jobKey.getGroup());
                map.put("description", "trigger:" + trigger.getKey());
                Trigger.TriggerState triggerState = sched.getTriggerState(trigger.getKey());
                map.put("jobStatus", triggerState.name());
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = (CronTrigger) trigger;
                    String cronExpression = cronTrigger.getCronExpression();
                    map.put("jobTime", cronExpression);
                }
                jobList.add(map);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return jobList;
    }

}
