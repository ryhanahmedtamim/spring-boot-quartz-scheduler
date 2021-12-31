package com.ryhan.test.scheduler.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


public class WebClientConfiguration {
  @Bean
  public WebClient.Builder webClientBuilder(){
    //return WebClient.builder();
    HttpClient httpClient = HttpClient.create()
        .tcpConfiguration(client ->
    client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100000)
        .doOnConnected(conn -> conn
        .addHandlerLast(new ReadTimeoutHandler(10))
        .addHandlerLast(new WriteTimeoutHandler(10))));

    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

    return WebClient.builder()
        .clientConnector(connector);
  }
}
