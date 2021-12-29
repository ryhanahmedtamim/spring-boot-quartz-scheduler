package com.unipet.bespoke.scheduler.jobs;

import com.unipet.bespoke.scheduler.info.TimerInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloWorldJob implements Job {

  @Override
  public void execute(JobExecutionContext context) {
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    TimerInfo info = (TimerInfo) jobDataMap.get(HelloWorldJob.class.getSimpleName());
    log.info("Remaining fire count is '{}'", info.getRemainingFireCount());
  }
}
