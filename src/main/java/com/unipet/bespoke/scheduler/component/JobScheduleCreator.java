package com.unipet.bespoke.scheduler.component;

import java.text.ParseException;
import java.util.Date;

import com.unipet.bespoke.scheduler.repository.schema.SchedulerJobInfo;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobScheduleCreator {

  public JobDetail createJob(Class<? extends QuartzJobBean> jobClass, boolean isDurable,
                             ApplicationContext context, SchedulerJobInfo jobInfo) {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(jobClass);
    factoryBean.setDurability(isDurable);
    factoryBean.setApplicationContext(context);
    factoryBean.setName(jobInfo.getJobName());
    factoryBean.setGroup(jobInfo.getJobGroup());
    factoryBean.setDescription(jobInfo.getJobDescription());
    // set job data map
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put(jobClass.getSimpleName(), jobInfo);
    factoryBean.setJobDataMap(jobDataMap);
    factoryBean.afterPropertiesSet();
    return factoryBean.getObject();
  }

  public CronTrigger createCronTrigger(Class<? extends QuartzJobBean> jobClass, Date startTime, String cronExpression, int misFireInstruction) {
    CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
    factoryBean.setName(jobClass.getSimpleName());
    factoryBean.setStartTime(startTime);
    factoryBean.setCronExpression(cronExpression);
    factoryBean.setMisfireInstruction(misFireInstruction);
    try {
      factoryBean.afterPropertiesSet();
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
    }
    return factoryBean.getObject();
  }

  public SimpleTrigger createSimpleTrigger(Class<? extends QuartzJobBean> jobClass, Date startTime, Long repeatTime, int misFireInstruction, int totalTriggerCount) {
    SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
    factoryBean.setName(jobClass.getSimpleName());
    factoryBean.setStartTime(startTime);
    factoryBean.setRepeatInterval(repeatTime);
    factoryBean.setRepeatCount(totalTriggerCount);
    factoryBean.setMisfireInstruction(misFireInstruction);
    factoryBean.afterPropertiesSet();
    return factoryBean.getObject();
  }
}