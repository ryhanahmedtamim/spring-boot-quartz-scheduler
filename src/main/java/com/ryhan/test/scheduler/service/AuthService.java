package com.ryhan.test.scheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {
  @Value("${keycloak.clientId}")
  String clientId;

  @Value("${keycloak.secret}")
  String secretKey;

  @Value("${keycloak.realm}")
  String realm;

  @Value("${keycloak.url}")
  String URL;

  public String clientLogin() {
    Map<String, Object> clientCredentials = new HashMap<>();
    clientCredentials.put("secret", secretKey);
    clientCredentials.put("grant_type", "client_credential");
    Configuration configuration =
        new Configuration(URL, realm, clientId, clientCredentials, null);
    AuthzClient authzClient = AuthzClient.create(configuration);

    AccessTokenResponse response = authzClient.obtainAccessToken();
    log.info("Response from keycloak : {} ", response );
    log.info("Token {}", response.getToken());
    return response.getToken();
  }
}
