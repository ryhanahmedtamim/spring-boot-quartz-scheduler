package com.unipet.bespoke.scheduler.info;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Setter
@Getter
public class TimerInfo implements Serializable {
  private int totalFireCount;
  private int remainingFireCount;
  private boolean runForever;
  private long repeatIntervalMs;
  private long initialOffsetMs;
  private String callbackData;
}