package com.unipet.bespoke.scheduler.service;

import java.util.*;
import java.util.stream.Collectors;

import com.unipet.bespoke.scheduler.component.JobScheduleCreator;
import com.unipet.bespoke.scheduler.dmain.CustomJobDetails;
import com.unipet.bespoke.scheduler.dmain.JobStatus;
import com.unipet.bespoke.scheduler.repository.schema.SchedulerJobInfo;
import com.unipet.bespoke.scheduler.jobs.SampleCronJob;
import com.unipet.bespoke.scheduler.jobs.SimpleJob;
import com.unipet.bespoke.scheduler.repository.jpa.SchedulerRepository;
import com.unipet.bespoke.scheduler.timerservice.SimpleTriggerListener;
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
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

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
    scheduler.getListenerManager().addTriggerListener(new SimpleTriggerListener(this, schedulerRepository));
    scheduler.start();
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
      SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
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
      SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
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
      SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
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
      SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
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
  public void saveOrUpdate(SchedulerJobInfo scheduleJob) throws Exception {
    if (scheduleJob.getCronExpression().length() > 0) {
      scheduleJob.setJobClass(SampleCronJob.class.getName());
      scheduleJob.setCronJob(true);
    } else {
      scheduleJob.setJobClass(SimpleJob.class.getName());
      scheduleJob.setCronJob(false);
      //scheduleJob.setRepeatTime((long) scheduleJob);
    }
    if (StringUtils.isEmpty(scheduleJob.getJobId())) {
      log.info("Job Info: {}", scheduleJob);
      scheduleNewJob(scheduleJob);
    } else {
      updateScheduleJob(scheduleJob);
    }
    scheduleJob.setDescOrder("i am job number " + scheduleJob.getJobId());
    scheduleJob.setInterfaceName("interface_" + scheduleJob.getJobId());
    log.info(">>>>> jobName = [" + scheduleJob.getJobName() + "]" + " created.");
  }

  @SuppressWarnings("unchecked")
  private void scheduleNewJob(SchedulerJobInfo jobInfo) {
    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();

      JobDetail jobDetail = JobBuilder
          .newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
          .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
      if (!scheduler.checkExists(jobDetail.getKey())) {

        jobDetail = scheduleCreator.createJob(
            (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), false, context,
           jobInfo);

        Trigger trigger;
        if (jobInfo.getCronJob()) {
          trigger = scheduleCreator.createCronTrigger((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), jobInfo.getStartDate(),
              jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {
          trigger = scheduleCreator.createSimpleTrigger((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), jobInfo.getStartDate(),
              jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo.getTotalTriggerCount());
        }
        scheduler.scheduleJob(jobDetail, trigger);
        jobInfo.setJobStatus(JobStatus.SCHEDULED.name());
        schedulerRepository.save(jobInfo);
        log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled.");
      } else {
        log.error("scheduleNewJobRequest.jobAlreadyExist");
      }
    } catch (ClassNotFoundException e) {
      log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  private void updateScheduleJob(SchedulerJobInfo jobInfo) throws ClassNotFoundException {
    Trigger newTrigger;
    if (jobInfo.getCronJob()) {
      newTrigger = scheduleCreator.createCronTrigger((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), new Date(),
          jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
    } else {
      newTrigger = scheduleCreator.createSimpleTrigger((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), new Date(), jobInfo.getRepeatTime(),
          SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo.getTotalTriggerCount());
    }
    try {
      schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), newTrigger);
      jobInfo.setJobStatus(JobStatus.EDITED_AND_SCHEDULED.name());
      schedulerRepository.save(jobInfo);
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
    try {
      final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
      if (jobDetail == null) {
        log.error("Failed to find timer with ID '{}'", jobKey);
        return;
      }

      jobDetail.getJobDataMap().put(jobName, info);

      scheduler.addJob(jobDetail, true, true);
    } catch (final SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }
  public Scheduler getScheduler(){
    return this.scheduler;
  }

  public Trigger getTrigger(TriggerKey triggerKey) throws SchedulerException {
    return scheduler.getTrigger(triggerKey);
  }

}
