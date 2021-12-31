package com.ryhan.test.scheduler.timerservice;


import com.ryhan.test.scheduler.util.TimerUtils;
import com.ryhan.test.scheduler.info.TimerInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchedulerService {
  private final Scheduler scheduler;

  @Autowired
  public SchedulerService(final Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public <T extends Job> void schedule(final Class<T> jobClass, final TimerInfo info) {
    final JobDetail jobDetail = TimerUtils.buildJobDetail(jobClass, info);
    final Trigger trigger = TimerUtils.buildTrigger(jobClass, info);

    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

//  public List<TimerInfo> getAllRunningTimers() {
//    try {
//      return scheduler.getJobKeys(GroupMatcher.anyGroup())
//          .stream()
//          .map(jobKey -> {
//            try {
//              final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
//              return (TimerInfo) jobDetail.getJobDataMap().get(jobKey.getName());
//            } catch (final SchedulerException e) {
//              log.error(e.getMessage(), e);
//              return null;
//            }
//          })
//          .filter(Objects::nonNull)
//          .collect(Collectors.toList());
//    } catch (final SchedulerException e) {
//      log.error(e.getMessage(), e);
//      return Collections.emptyList();
//    }
//  }
//
//  public TimerInfo getRunningTimer(final String timerId) {
//    try {
//      final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(timerId));
//      if (jobDetail == null) {
//        log.error("Failed to find timer with ID '{}'", timerId);
//        return null;
//      }
//
//      return (TimerInfo) jobDetail.getJobDataMap().get(timerId);
//    } catch (final SchedulerException e) {
//      log.error(e.getMessage(), e);
//      return null;
//    }
//  }
//

//
//  public Boolean deleteTimer(final String timerId) {
//    try {
//      return scheduler.deleteJob(new JobKey(timerId));
//    } catch (SchedulerException e) {
//      log.error(e.getMessage(), e);
//      return false;
//    }
//  }
//
//  @PostConstruct
//  public void init() {
//    try {
//      scheduler.start();
//      scheduler.getListenerManager().addTriggerListener(new SimpleTriggerListener(this));
//    } catch (SchedulerException e) {
//      log.error(e.getMessage(), e);
//    }
//  }
//
//  @PreDestroy
//  public void preDestroy() {
//    try {
//      scheduler.shutdown();
//    } catch (SchedulerException e) {
//      log.error(e.getMessage(), e);
//    }
//  }
}

