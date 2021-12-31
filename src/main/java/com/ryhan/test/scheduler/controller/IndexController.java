package com.ryhan.test.scheduler.controller;


import java.util.List;

import com.ryhan.test.scheduler.service.SchedulerJobService;
import com.ryhan.test.scheduler.repository.schema.SchedulerJobInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  private final SchedulerJobService scheduleJobService;

  public IndexController(SchedulerJobService scheduleJobService) {
    this.scheduleJobService = scheduleJobService;
  }

  @GetMapping("/index")
  public String index(Model model){
    List<SchedulerJobInfo> jobList = scheduleJobService.getAllJobList();
    model.addAttribute("jobs", jobList);
    return "index";
  }

}
