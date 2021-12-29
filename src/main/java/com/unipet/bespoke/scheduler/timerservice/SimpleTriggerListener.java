package com.unipet.bespoke.scheduler.timerservice;

import com.unipet.bespoke.scheduler.repository.jpa.SchedulerRepository;
import com.unipet.bespoke.scheduler.repository.schema.SchedulerJobInfo;
import com.unipet.bespoke.scheduler.service.SchedulerJobService;
import org.quartz.*;
import org.springframework.beans.BeanUtils;

public class SimpleTriggerListener implements TriggerListener {
  private final SchedulerJobService schedulerJobService;
  private final SchedulerRepository schedulerRepository;

  public SimpleTriggerListener(SchedulerJobService schedulerJobService, SchedulerRepository schedulerRepository) {
    this.schedulerJobService = schedulerJobService;
    this.schedulerRepository = schedulerRepository;
  }


  @Override
  public String getName() {
    return SimpleTriggerListener.class.getSimpleName();
  }

  @Override
  public void triggerFired(Trigger trigger, JobExecutionContext context) {
    String jobId = trigger.getKey().getName();
    String jobGroup = "";
    String jobName = "";

    final JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    try{
      var tempInfo = jobDataMap.get(jobId);
      SchedulerJobInfo info = new SchedulerJobInfo();
      BeanUtils.copyProperties(tempInfo, info);
      jobGroup = info.getJobGroup();
      jobName = info.getJobName();
      info = schedulerRepository.findByJobName(jobName);

      if(info.getCronJob()){
        info.setRemainingFireCount(-1);
      }else if(info.getTotalTriggerLimit() > 0) {
        info.setRemainingFireCount(info.getTotalTriggerLimit() - info.getTotalTriggerCount());
      }
      info.setTotalTriggerCount(info.getTotalTriggerCount() + 1);
      schedulerJobService.updateTimer(new JobKey(jobName, jobGroup), jobId, info);
    }
    catch (Exception e){

    }

  }

  /**
   * @param trigger
   * @param context
   * @return
   *     We have a method in TriggerListener vetoJobExecution(). This method is executed when
   *     the trigger is just fired. So, with this we can thereby control whether to execute or
   *     dismiss the job associated with the trigger. If we want to dismiss the job , then we should
   *     return true from this method.
   *     <p>As soon as ,we returned from this method, "jobExecutionVetoed()" method inside our
   *     joblistener will be executed to intimate that the job execution has been banned(vetoed).
   */
  @Override
  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    return false;
  }

  @Override
  public void triggerMisfired(Trigger trigger) {

  }

  @Override
  public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {

  }
}

