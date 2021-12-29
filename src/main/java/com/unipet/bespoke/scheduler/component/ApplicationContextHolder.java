//package com.unipet.bespoke.scheduler.component;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.scheduling.quartz.SpringBeanJobFactory;
//import org.springframework.stereotype.Component;
//
//@Component
//public final class ApplicationContextHolder extends SpringBeanJobFactory implements ApplicationContextAware {
//
//  private static ApplicationContext context;
//
//  private transient AutowireCapableBeanFactory beanFactory;
//
//  @Override
//  public void setApplicationContext(ApplicationContext ctx) throws BeansException {
//    beanFactory = ctx.getAutowireCapableBeanFactory();
//    context = ctx;
//  }
//
//  public static ApplicationContext getContext() {
//    return context;
//  }
//}