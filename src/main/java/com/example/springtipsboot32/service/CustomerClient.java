package com.example.springtipsboot32.service;

import com.example.springtipsboot32.client.MyRestClient;
import com.example.springtipsboot32.model.APIResponseDTO;
import com.example.springtipsboot32.model.APIResponseDTO.CustomerDTO;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerClient {

  private final MyRestClient restClient;

  private static <T> ParameterizedTypeReference<APIResponseDTO<T>> getParameterizedTypeReference(
      Class<T> clazz) {
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
            return APIResponseDTO.class;
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
      var apiResponseDTO = invokeAPI("http://localhost:8081/testcustomer", null,
          HttpMethod.POST,
          CustomerDTO.class);
      return Optional.of(apiResponseDTO.getResult());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private <T, R> APIResponseDTO<T> invokeAPI(String apiUrl, R requestDTO, HttpMethod httpMethod,
      Class<T> responseType) {
    var responseDTOClass = getParameterizedTypeReference(responseType);
    return restClient.exchange(
        apiUrl,
        requestDTO,
        httpMethod,
        responseDTOClass
    );
  }
}
