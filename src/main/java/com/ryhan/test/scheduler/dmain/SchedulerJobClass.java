package com.ryhan.test.scheduler.dmain;

import java.io.Serializable;

public enum SchedulerJobClass implements Serializable {
  SubscriptionAutoRenewScheduler("com.unipet.bespoke.scheduler.jobs.SubscriptionAutoRenewScheduler.class"),
  SampleCronJob("com.unipet.bespoke.scheduler.jobs.SampleCronJob.class"),
  AdminTestScheduler("com.unipet.bespoke.scheduler.jobs.AdminTestScheduler");
  private String label;
  SchedulerJobClass(String label) {
    this.label = label;
  }

  public String getLabel() {
    return this.label;
  }
}
