package com.ryhan.test.scheduler.jobs;

import com.ryhan.test.scheduler.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class AdminTestScheduler extends QuartzJobBean {


  @Autowired
  AuthService authService;

  @Autowired
  WebClient.Builder builder;

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

    doWork();

  }
  @Async
  void doWork(){
    final String url = "localhost:5051/scheduler";
    try{
      log.info("Amin test scheduler started----------->");
      builder.build().get().uri(url)
          .header("Authorization", "Bearer "+ authService.clientLogin())//.exchange();
          .retrieve()
          .onStatus(
              status -> {
                log.info("response got from adimin service with status {}", status);
                return status.value() == 200;
              },
              clientResponse -> Mono.empty()
          )
          .bodyToMono(Void.class).retry(2)
          .doOnError(e -> log.error("Boom!", e))
//          .map(m->{
//            log.info("Got response from admin service");
//            return null;
//          })
          .block();
      log.info("Amin test scheduler end----------->");
    }
    catch (Exception e)
    {
      log.error("Error occurred during admin test scheduler{}", e.getMessage());
    }
  }
}
