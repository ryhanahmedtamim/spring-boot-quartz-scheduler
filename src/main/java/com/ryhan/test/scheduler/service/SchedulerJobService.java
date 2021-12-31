package com.ryhan.test.scheduler.service;

import java.util.*;
import java.util.stream.Collectors;

import com.ryhan.test.scheduler.component.JobScheduleCreator;
import com.ryhan.test.scheduler.dmain.JobStatus;
import com.ryhan.test.scheduler.dmain.JobRequest;
import com.ryhan.test.scheduler.dmain.TriggerDetails;
import com.ryhan.test.scheduler.exceptions.JobAlreadyExistsException;
import com.ryhan.test.scheduler.exceptions.JobNotFoundException;
import com.ryhan.test.scheduler.repository.jpa.SchedulerRepository;
import com.ryhan.test.scheduler.timerservice.SimpleTriggerListener;
import com.ryhan.test.scheduler.dmain.CustomJobDetails;
import com.ryhan.test.scheduler.repository.schema.SchedulerJobInfo;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Transactional
@Service
public class SchedulerJobService {

  private Scheduler scheduler;
  private final SchedulerFactoryBean schedulerFactoryBean;
  private final SchedulerRepository schedulerRepository;
  private final ApplicationContext context;
  private final JobScheduleCreator scheduleCreator;

  public SchedulerJobService(SchedulerFactoryBean schedulerFactoryBean, SchedulerRepository schedulerRepository, ApplicationContext context, JobScheduleCreator scheduleCreator) {
    //this.scheduler = scheduler;
    this.schedulerFactoryBean = schedulerFactoryBean;
    this.schedulerRepository = schedulerRepository;
    this.context = context;
    this.scheduleCreator = scheduleCreator;
  }

  @PostConstruct
  private void init() throws SchedulerException {
    scheduler = schedulerFactoryBean.getScheduler();
    log.info("scheduler name : {}", scheduler.getSchedulerName());
    scheduler.getListenerManager().addTriggerListener(new SimpleTriggerListener(this, schedulerRepository));
    scheduler.start();
  }

  @PreDestroy
  public void preDestroy() {
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  public SchedulerMetaData getMetaData() throws SchedulerException {
    SchedulerMetaData metaData = scheduler.getMetaData();
    return metaData;
  }

  public List<SchedulerJobInfo> getAllJobList() {
    return schedulerRepository.findAll();
  }

  @Transactional
  public boolean deleteJob(SchedulerJobInfo jobInfo) {
    try {
      SchedulerJobInfo getJobInfo = schedulerRepository.findByJobNameAndDeletedIsFalse(jobInfo.getJobName());
      getJobInfo.setJobStatus(JobStatus.DELETED.name());
      getJobInfo.setDeleted(true);
      schedulerRepository.save(getJobInfo);
      log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " deleted.");
      return schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
    } catch (SchedulerException e) {
      log.error("Failed to delete job - {}", jobInfo.getJobName(), e);
      return false;
    }
  }

  public boolean pauseJob(SchedulerJobInfo jobInfo) {
    try {
      SchedulerJobInfo getJobInfo = schedulerRepository.findByJobNameAndDeletedIsFalse(jobInfo.getJobName());
      getJobInfo.setJobStatus(JobStatus.PAUSED.name());
      schedulerRepository.save(getJobInfo);
      schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
      log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " paused.");
      return true;
    } catch (SchedulerException e) {
      log.error("Failed to pause job - {}", jobInfo.getJobName(), e);
      return false;
    }
  }

  public boolean resumeJob(SchedulerJobInfo jobInfo) {
    try {
      SchedulerJobInfo getJobInfo = schedulerRepository.findByJobNameAndDeletedIsFalse(jobInfo.getJobName());
      getJobInfo.setJobStatus(JobStatus.RESUMED.name());
      schedulerRepository.save(getJobInfo);
      schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
      log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " resumed.");
      return true;
    } catch (SchedulerException e) {
      log.error("Failed to resume job - {}", jobInfo.getJobName(), e);
      return false;
    }
  }

  public boolean startJobNow(SchedulerJobInfo jobInfo) {
    try {
      SchedulerJobInfo getJobInfo = schedulerRepository.findByJobNameAndDeletedIsFalse(jobInfo.getJobName());
      getJobInfo.setJobStatus(JobStatus.SCHEDULED_AND_STARTED.name());
      schedulerRepository.save(getJobInfo);
      schedulerFactoryBean.getScheduler().triggerJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
      log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled and started now.");
      return true;
    } catch (SchedulerException e) {
      log.error("Failed to start new job - {}", jobInfo.getJobName(), e);
      return false;
    }
  }

  @SuppressWarnings("deprecation")
  @Transactional
  public void save(JobRequest scheduleJob) throws Exception {
     var scheduleJobEntity = new SchedulerJobInfo();
     BeanUtils.copyProperties(scheduleJob, scheduleJobEntity, "jobClass", "timeZone");
     scheduleJobEntity.setJobClass(scheduleJob.getJobClass().getLabel());
     //scheduleJobEntity.setTimeZone(scheduleJob);
    log.info("Job Info: {}", scheduleJob);
    scheduleJobEntity.setJobStatus(JobStatus.SCHEDULED.name());
    schedulerRepository.save(scheduleJobEntity);

    scheduleNewJob(scheduleJob);
    log.info(">>>>> jobName = [" + scheduleJob.getJobName() + "]" + " created.");
  }

  @Transactional
  public void update(JobRequest scheduleJob) throws Exception {
    var scheduleJobEntity = schedulerRepository.findByJobNameAndDeletedIsFalse(scheduleJob.getJobName());
    // TODO : use mapper
    BeanUtils.copyProperties(scheduleJob, scheduleJobEntity, "jobId", "jobClass", "timeZone");
    scheduleJobEntity.setJobClass(scheduleJob.getJobClass().getLabel());
    scheduleJobEntity.setJobStatus(JobStatus.EDITED_AND_SCHEDULED.name());
    log.info("Job Info: {}", scheduleJob);

    schedulerRepository.save(scheduleJobEntity);

    updateScheduleJob(scheduleJob);
    log.info(">>>>> jobName = [" + scheduleJob.getJobName() + "]" + " created.");
  }

  @SuppressWarnings("unchecked")
  private void scheduleNewJob(JobRequest jobInfo) throws SchedulerException, ClassNotFoundException {

//      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      Class<? extends QuartzJobBean> jobClass = (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass().getLabel());
      JobDetail jobDetail = JobBuilder
          .newJob(jobClass)
          .withIdentity(jobClass.getSimpleName(), jobClass.getSimpleName()).build();
      if (!scheduler.checkExists(jobDetail.getKey())) {

        jobDetail = scheduleCreator.createJob(
            jobClass, false, context, jobInfo);

        Trigger trigger;
        if (jobInfo.getCronJob()) {
          trigger = scheduleCreator.createCronTrigger(jobClass, jobInfo.getStartDate(),
              jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo.getTimeZone());
        } else {
          trigger = scheduleCreator.createSimpleTrigger(jobClass, jobInfo.getStartDate(),
              jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo.getTotalTriggerLimit());
        }
        scheduler.scheduleJob(jobDetail, trigger);

        log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled.");
      } else {
        log.error("scheduleNewJobRequest.jobAlreadyExist");
        throw new JobAlreadyExistsException("Job Already Exist");
      }
  }

  private void updateScheduleJob(JobRequest jobInfo) throws ClassNotFoundException, SchedulerException {
    Class<? extends QuartzJobBean> jobClass = (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass().getLabel());
    JobDetail jobDetail = JobBuilder
        .newJob(jobClass)
        .withIdentity(jobClass.getSimpleName(), jobClass.getSimpleName()).build();
    if(!scheduler.checkExists(jobDetail.getKey())){
      throw new JobNotFoundException("No such job found whit this job id" + jobInfo.getJobName());
    }
    Trigger newTrigger;
    if (jobInfo.getCronJob()) {
      newTrigger = scheduleCreator.createCronTrigger(jobClass, jobInfo.getStartDate(),
          jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo.getTimeZone());
    } else {
      newTrigger = scheduleCreator.createSimpleTrigger(jobClass, jobInfo.getStartDate(), jobInfo.getRepeatTime(),
          SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo.getTotalTriggerCount());
    }
    try {
      scheduler.rescheduleJob(TriggerKey.triggerKey(jobClass.getSimpleName(), jobClass.getSimpleName()), newTrigger);
      log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " updated and scheduled.");
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  public List<CustomJobDetails> getAllRunningSchedule() {
    try {
      return scheduler.getJobKeys(GroupMatcher.anyGroup())
          .stream()
          .map(jobKey -> {
            try {
              var jobDetails = scheduler.getJobDetail(jobKey);
              var customJobDetails = new CustomJobDetails();
              BeanUtils.copyProperties(jobDetails, customJobDetails);
              customJobDetails.setJobKey(jobKey);
              customJobDetails.setJobClass(jobDetails.getJobClass().getSimpleName());
              return customJobDetails;
            } catch (final SchedulerException e) {
              log.error(e.getMessage(), e);
              return null;
            }
          })
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    } catch (final SchedulerException e) {
      log.error(e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  public void updateTimer(final JobKey jobKey, String jobName, final SchedulerJobInfo info) throws SchedulerException {
    //var jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
      final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
      if (jobDetail == null) {
        log.error("Failed to find timer with ID '{}'", jobKey);
        return;
      }
      jobDetail.getJobDataMap().put(jobName, info);
      scheduler.addJob(jobDetail, true, true);
  }
  public Scheduler getScheduler(){
    return this.scheduler;
  }

  public Trigger getTrigger(TriggerKey triggerKey) throws SchedulerException {
    return scheduler.getTrigger(triggerKey);
  }

  public List<TriggerDetails> getAllTrigger() throws SchedulerException {
    return scheduler.getTriggerKeys(GroupMatcher.anyGroup()).stream().map(tKey->{
      try {
        var tempTrigger = scheduler.getTrigger(tKey);
        var triggerDetails = new TriggerDetails();
        BeanUtils.copyProperties(tempTrigger, triggerDetails);
        triggerDetails.setJobDataMap(tempTrigger.getJobDataMap());
        triggerDetails.setCornEx(((CronTriggerImpl)tempTrigger).getCronExpression());
        return triggerDetails;
      } catch (SchedulerException e) {
        e.printStackTrace();
        return null;
      }
    }).filter(Objects::nonNull).collect(Collectors.toList());
  }

}
