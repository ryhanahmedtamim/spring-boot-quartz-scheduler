package com.unipet.bespoke.scheduler.controller;

import java.util.List;
import java.util.Set;

import com.unipet.bespoke.scheduler.dmain.CustomJobDetails;
import com.unipet.bespoke.scheduler.dmain.Message;
import com.unipet.bespoke.scheduler.repository.schema.SchedulerJobInfo;
import com.unipet.bespoke.scheduler.service.SchedulerJobService;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api", consumes = "*/*")
public class JobController {

  private final SchedulerJobService scheduleJobService;

  @PostMapping(value = "/save-or-update")
  public Object saveOrUpdate(@RequestBody SchedulerJobInfo job) {
    log.info("params, job = {}", job);
    Message message = Message.failure();
    try {
      scheduleJobService.saveOrUpdate(job);
      message = Message.success();
    } catch (Exception e) {
      message.setMsg(e.getMessage());
      log.error("updateCron ex:", e);
    }
    return message;
  }

  @GetMapping("/meta-data")
  public Object metaData() throws SchedulerException {
    SchedulerMetaData metaData = scheduleJobService.getMetaData();
    return metaData;
  }

  @GetMapping("/get-all-jobs")
  public Object getAllJobs() throws SchedulerException {
    List<SchedulerJobInfo> jobList = scheduleJobService.getAllJobList();
    return jobList;
  }

  @RequestMapping(value = "/run-job", method = { RequestMethod.GET, RequestMethod.POST })
  public Object runJob(SchedulerJobInfo job) {
    log.info("params, job = {}", job);
    Message message = Message.failure();
    try {
      scheduleJobService.startJobNow(job);
      message = Message.success();
    } catch (Exception e) {
      message.setMsg(e.getMessage());
      log.error("runJob ex:", e);
    }
    return message;
  }

  @RequestMapping(value = "/pauseJob", method = { RequestMethod.GET, RequestMethod.POST })
  public Object pauseJob(SchedulerJobInfo job) {
    log.info("params, job = {}", job);
    Message message = Message.failure();
    try {
      scheduleJobService.pauseJob(job);
      message = Message.success();
    } catch (Exception e) {
      message.setMsg(e.getMessage());
      log.error("pauseJob ex:", e);
    }
    return message;
  }

  @RequestMapping(value = "/resume-job", method = { RequestMethod.GET, RequestMethod.POST })
  public Object resumeJob(SchedulerJobInfo job) {
    log.info("params, job = {}", job);
    Message message = Message.failure();
    try {
      scheduleJobService.resumeJob(job);
      message = Message.success();
    } catch (Exception e) {
      message.setMsg(e.getMessage());
      log.error("resumeJob ex:", e);
    }
    return message;
  }

  @RequestMapping(value = "/delete-job", method = { RequestMethod.GET, RequestMethod.POST })
  public Object deleteJob(SchedulerJobInfo job) {
    log.info("params, job = {}", job);
    Message message = Message.failure();
    try {
      scheduleJobService.deleteJob(job);
      message = Message.success();
    } catch (Exception e) {
      message.setMsg(e.getMessage());
      log.error("deleteJob ex:", e);
    }
    return message;
  }

  @GetMapping("/get-all-jobs-details")
  public Object getAllJobsDetails() throws SchedulerException {
    List<CustomJobDetails> jobList = scheduleJobService.getAllRunningSchedule();
    return jobList;
  }
}
