package com.ryhan.test.scheduler.controller;

import java.util.List;

import com.ryhan.test.scheduler.dmain.JobRequest;
import com.ryhan.test.scheduler.service.SchedulerJobService;
import com.ryhan.test.scheduler.dmain.CustomJobDetails;
import com.ryhan.test.scheduler.dmain.Message;
import com.ryhan.test.scheduler.repository.schema.SchedulerJobInfo;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api", consumes = "*/*")
public class JobController {

  private final SchedulerJobService scheduleJobService;

  @PostMapping(value = "/add-schedule")
  public Object save(@RequestBody @Validated JobRequest job) {
    log.info("params, job = {}", job);
    Message message = Message.failure();
    try {
      scheduleJobService.save(job);
      message = Message.success();
    } catch (Exception e) {
      message.setMsg(e.getMessage());
      log.error("updateCron ex:", e);
    }
    return message;
  }
  @PutMapping(value = "/update-schedule")
  public Object update(@RequestBody @Validated JobRequest job) {
    log.info("params, job = {}", job);
    Message message = Message.failure();
    try {
      scheduleJobService.update(job);
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

  @PostMapping(value = "/run-job")
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

  @PostMapping(value = "/pauseJob")
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

  @PostMapping(value = "/resume-job")
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

  @DeleteMapping(value = "/delete-job")
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
