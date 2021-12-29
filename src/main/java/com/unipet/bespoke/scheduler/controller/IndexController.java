package com.unipet.bespoke.scheduler.controller;


import java.util.List;

import com.unipet.bespoke.scheduler.repository.schema.SchedulerJobInfo;
import com.unipet.bespoke.scheduler.service.SchedulerJobService;
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
