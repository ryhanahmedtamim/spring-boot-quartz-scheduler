package com.unipet.bespoke.scheduler.jobs;

import java.util.stream.IntStream;

import com.unipet.bespoke.scheduler.repository.jpa.SchedulerRepository;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleJob extends QuartzJobBean {
  @Autowired
  private SchedulerRepository schedulerRepository;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    log.info("SimpleJob Start................");
    IntStream.range(0, 5).forEach(i -> {
      log.info("Counting - {}", i);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error(e.getMessage(), e);
      }
    });
    log.info("SimpleJob End................");
    var jobName = context.getJobDetail().getKey().getName();
    var job = schedulerRepository.findByJobName(jobName);
    if(job!=null){
      job.setTotalTriggerCount(job.getTotalTriggerCount() != null ? job.getTotalTriggerCount() + 1: 1);
      job.setRemainingFireCount(job.getTotalTriggerLimit() - job.getTotalTriggerCount());
      schedulerRepository.save(job);
    }
  }
}

