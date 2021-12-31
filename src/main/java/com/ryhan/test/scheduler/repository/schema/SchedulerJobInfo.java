package com.ryhan.test.scheduler.repository.schema;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

@ToString
@Getter
@Setter
@Entity
@Table(name = "scheduler_job_info")
public class SchedulerJobInfo implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long jobId;
  private String jobName;
  private String jobGroup;
  private String jobStatus;
  private String jobClass;
  private String cronExpression;
//  private String descOrder;
//  private String interfaceName;
  private Long repeatTime;
  private Boolean cronJob;
  private Integer totalTriggerLimit = -1;
  private boolean deleted = false;
  private Date startDate;
  private String timeZone;
  private Integer totalTriggerCount = 0;
  private String jobDescription;
  private int remainingFireCount = -1;
  private Date lastTriggerTime;
}
