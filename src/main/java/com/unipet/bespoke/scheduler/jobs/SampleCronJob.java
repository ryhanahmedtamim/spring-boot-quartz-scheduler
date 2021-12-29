package com.unipet.bespoke.scheduler.jobs;

import java.util.stream.IntStream;

import com.unipet.bespoke.scheduler.repository.jpa.SchedulerRepository;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@DisallowConcurrentExecution
@Component
public class SampleCronJob extends QuartzJobBean {
  @Autowired
 private SchedulerRepository schedulerRepository;



  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

//    ApplicationContext applicationContext = null;
//    try {
//      applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContex");
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    SchedulerRepository schedulerRepository = (SchedulerRepository) applicationContext.getBean(SchedulerRepository.class);

    log.info("SampleCronJob Start................");
    IntStream.range(0, 5).forEach(i -> {
      log.info("Counting - {}", i);
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        log.error(e.getMessage(), e);
      }
    });
    log.info("SampleCronJob End................");
    var jobName = context.getJobDetail().getKey().getName();
    var job = schedulerRepository.findByJobName(jobName);
    if(job!=null){
      job.setTotalTriggerCount(job.getTotalTriggerCount() != null ? job.getTotalTriggerCount() + 1: 1);
      schedulerRepository.save(job);
    }
  }
}

