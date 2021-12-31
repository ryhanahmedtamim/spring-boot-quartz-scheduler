package com.ryhan.test.scheduler.jobs;

import com.ryhan.test.scheduler.repository.jpa.SchedulerRepository;
import com.ryhan.test.scheduler.service.AuthService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@DisallowConcurrentExecution
@Component
public class SampleCronJob extends QuartzJobBean {
  @Autowired
 private SchedulerRepository schedulerRepository;
  @Autowired
  private AuthService authService;



  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    authService.clientLogin();
    log.info("SampleCronJob Start................");
    var jobName = context.getJobDetail().getKey().getName();
    var job = schedulerRepository.findByJobNameAndDeletedIsFalse(jobName);
    log.info("SampleCronJob End................");
    if(job!=null){
      job.setTotalTriggerCount(job.getTotalTriggerCount() != null ? job.getTotalTriggerCount() + 1: 1);
      schedulerRepository.save(job);
    }
  }
}

