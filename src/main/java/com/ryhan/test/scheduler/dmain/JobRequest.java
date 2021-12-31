package com.ryhan.test.scheduler.dmain;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class JobRequest implements Serializable {
  private Long jobId;
  private String jobName;
  private String jobGroup;
  private String jobStatus;

  @NotNull
  private SchedulerJobClass jobClass;
  private String cronExpression;
//  private String descOrder;
//  private String interfaceName;
  private Long repeatTime;
  @NotNull
  private Boolean cronJob;
  private Integer totalTriggerLimit = -1;
  private Date startDate;
  private TimeZone timeZone;
  private Integer totalTriggerCount = 0;
  private String jobDescription;
  private int remainingFireCount = -1;
  private Date lastTriggerTime;
}
