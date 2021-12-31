package com.ryhan.test.scheduler.dmain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.quartz.JobDataMap;
import org.quartz.JobKey;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CustomJobDetails {
  private JobKey jobKey;
  private String description;
  private String jobClass;
  private JobDataMap jobDataMap;
  private boolean durable;
  private boolean concurrentRunningAllow;
  private boolean recoverRequest;


}
