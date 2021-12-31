package com.ryhan.test.scheduler.jobs;

import com.ryhan.test.scheduler.service.AuthService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.reactive.function.client.WebClient;

public class SubscriptionAutoRenewScheduler extends QuartzJobBean {

  @Autowired
  WebClient webClient;

  @Autowired
  AuthService authService;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    final String url = "localhost:5051/scheduler";
   webClient.get().uri(url)
       .header("Authorization", "Bearer "+ authService.clientLogin())
       .retrieve();
  }
}
