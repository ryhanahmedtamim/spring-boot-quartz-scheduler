package com.unipet.bespoke.scheduler.configuration;


import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerConfig {
  private final DataSource dataSource;
  private final ApplicationContext applicationContext;
  private final QuartzProperties quartzProperties;

  public SchedulerConfig(DataSource dataSource, ApplicationContext applicationContext, QuartzProperties quartzProperties) {
    this.dataSource = dataSource;
    this.applicationContext = applicationContext;
    this.quartzProperties = quartzProperties;
  }


  @Bean
  public SchedulerFactoryBean schedulerFactoryBean() {

    SchedulerJobFactory jobFactory = new SchedulerJobFactory();
    jobFactory.setApplicationContext(applicationContext);

    Properties properties = new Properties();
    properties.putAll(quartzProperties.getProperties());

    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setOverwriteExistingJobs(true);
    factory.setDataSource(dataSource);
    factory.setQuartzProperties(properties);
    factory.setJobFactory(jobFactory);
    return factory;
  }
}
