package com.ryhan.test.scheduler.dmain;

import lombok.*;
import org.quartz.CronExpression;
import org.quartz.JobDataMap;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import java.util.Date;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TriggerDetails {
  private String cornEx;
  private Trigger.TriggerState triggerState;
  private Trigger.CompletedExecutionInstruction completedExecutionInstruction;
  private TriggerKey triggerKey;
  private String description;
  private String calenderName;
  private JobDataMap jobDataMap;
  private int priority;
  private boolean myFireAgain;
  private Date startTime;
  private Date endTime;
  private Date nextFireTime;
  private Date previousFireTime;
  private Date fireTimeAfter;
  private Date finalFireTime;
  /**
   * Get the instruction the Scheduler should be given for handling misfire situations for this
   * Trigger- the concrete Trigger type that you are using will have defined a set of additional
   * MISFIRE_INSTRUCTION_XXX constants that may be set as this property's value. If not explicitly
   * set, the default value is MISFIRE_INSTRUCTION_SMART_POLICY. See Also:
   * MISFIRE_INSTRUCTION_SMART_POLICY, SimpleTrigger, CronTrigger
   */
  private int misfireInstruction;
}
