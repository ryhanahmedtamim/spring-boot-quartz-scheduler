package com.unipet.bespoke.scheduler.configuration;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.ResourceUtils;


@Slf4j
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
  public SchedulerFactoryBean schedulerFactoryBean() throws FileNotFoundException {

    SchedulerJobFactory jobFactory = new SchedulerJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    Properties properties = new Properties();
    properties.putAll(quartzProperties.getProperties());
    log.info("Quartz properties : {}", quartzProperties.getProperties());

    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setOverwriteExistingJobs(true);
    factory.setQuartzProperties(properties);
    factory.setJobFactory(jobFactory);
   // factory.setConfigLocation(new ClassPathResource("quartz.properties"));
    factory.setDataSource(dataSource);
    return factory;
  }
}
