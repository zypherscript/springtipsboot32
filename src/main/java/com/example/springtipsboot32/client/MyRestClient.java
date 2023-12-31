package com.example.springtipsboot32.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MyRestClient extends RestTemplate {

  private <T> ResponseEntity<T> execute(
      String uri,
      HttpMethod method,
      HttpEntity<?> requestEntity,
      ParameterizedTypeReference<T> responseType) {
    return super.exchange(uri, method, requestEntity, responseType);
  }

  public <T, R> R exchange(
      String apiUrl,
      T requestDTO,
      HttpMethod httpMethod,
      ParameterizedTypeReference<R> responseType) {
    var responseEntity = execute(apiUrl, httpMethod, new HttpEntity<>(requestDTO), responseType);
    if (responseEntity.getStatusCode().value() >= HttpStatus.OK.value()
        && responseEntity.getStatusCode().value() <= HttpStatus.IM_USED.value()) {
      return responseEntity.getBody();
    } else {
      throw new RuntimeException();
    }
  }
}
