package com.example.springtipsboot32.service;

import com.example.springtipsboot32.client.MyRestClient;
import com.example.springtipsboot32.model.APIResponseDTO;
import com.example.springtipsboot32.model.APIResponseDTO.CustomerDTO;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerClient {

  private final MyRestClient restClient;

  private static <T, U> ParameterizedTypeReference<U> getParameterizedTypeReference(
      Class<T> clazz, Class<U> mainClazz) {
    return new ParameterizedTypeReference<>() {
      @Override
      public Type getType() {
        return new ParameterizedType() {
          @Override
          public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
          }

          @Override
          public Type getRawType() {
            return mainClazz;
          }

          @Override
          public Type getOwnerType() {
            return null;
          }
        };
      }
    };
  }

  public Optional<CustomerDTO> getCustomer() {
    try {
      var apiResponseDTO = invokeAPI("http://localhost:8081/test",
          null,
          HttpMethod.POST,
          CustomerDTO.class,
          APIResponseDTO.class);
      var customer = (CustomerDTO) apiResponseDTO.getResult();
      return Optional.of(customer);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

  private <T, R, U> U invokeAPI(String apiUrl, R requestDTO, HttpMethod httpMethod,
      Class<T> resultType, Class<U> responseType) {
    var responseDTOClass = getParameterizedTypeReference(resultType, responseType);
    return restClient.exchange(
        apiUrl,
        requestDTO,
        httpMethod,
        responseDTOClass
    );
  }
}
