package com.ryhan.test.scheduler.dmain;

import java.io.Serializable;

public enum SchedulerJobClass implements Serializable {
  SubscriptionAutoRenewScheduler(com.ryhan.test.scheduler.jobs.SubscriptionAutoRenewScheduler.class.getName()),
  SampleCronJob(com.ryhan.test.scheduler.jobs.SampleCronJob.class.getName()),
  AdminTestScheduler(com.ryhan.test.scheduler.jobs.AdminTestScheduler.class.getName());
  private String label;
  SchedulerJobClass(String label) {
    this.label = label;
  }
  public String getLabel() {
    return this.label;
  }
}
