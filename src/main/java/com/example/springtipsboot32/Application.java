package com.example.springtipsboot32;

import com.example.springtipsboot32.client.MyRestClient;
import com.example.springtipsboot32.model.APIResponseDTO;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
@Slf4j
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

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

  public <T, R> APIResponseDTO<T> invokeAPI(R requestDTO, HttpMethod httpMethod,
      Class<T> responseType) {
    String productAPI = "http://localhost:8081/customers";
    var responseDTOClass = getParameterizedTypeReference(responseType);
    return restClient.exchange(
        productAPI,
        requestDTO,
        httpMethod,
        responseDTOClass
    );
  }

  @Autowired
  private MyRestClient restClient;

  @Bean
  RestClient restClient(RestClient.Builder builder) {
    return builder
        .requestFactory(new JdkClientHttpRequestFactory())
        .build();
  }

  @Bean
  CatFactClient2 catFactClient2(RestClient restClient) {
    return HttpServiceProxyFactory
        .builder()
        .exchangeAdapter(RestClientAdapter.create(restClient))
        .build()
        .createClient(CatFactClient2.class);
  }

  @Bean
  @Profile("!default")
  ApplicationRunner loom() {
    return args -> {
      var observed = new ConcurrentSkipListSet<String>();
      var threads = new ArrayList<Thread>();
      for (int i = 0; i < 1_000; i++) {
        var ind = i;
        threads.add(Thread.ofVirtual().unstarted(new Runnable() {
          @Override
          public void run() {
            if (ind == 0) {
              observed.add(Thread.currentThread().toString());
            }
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
            if (ind == 0) {
              observed.add(Thread.currentThread().toString());
            }

            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
            if (ind == 0) {
              observed.add(Thread.currentThread().toString());
            }

            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
            if (ind == 0) {
              observed.add(Thread.currentThread().toString());
            }

            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
            if (ind == 0) {
              observed.add(Thread.currentThread().toString());
            }
          }
        }));
      }
      for (var t : threads) {
        t.start();
      }
      for (var t : threads) {
        t.join();
      }
      System.out.println(observed);
    };
  }

  String displayUserForLoan(Loan loan) {
    return switch (loan) {
//      case UnsecuredLoan usl -> "unsecuredLoad with interest " + usl.interest();
      case UnsecuredLoan(var interest) -> "unsecuredLoad with interest " + interest;
      case SecuredLoan sl -> "securedLoan";
    };
//    if (loan instanceof SecuredLoan sl) {
//      return "securedLoan";
//    }
//    if (loan instanceof UnsecuredLoan usl) {
//      return "unsecuredLoad with interest " + usl.interest();
//    }
//    return null;
  }
}

sealed interface Loan permits SecuredLoan, UnsecuredLoan {

}

final class SecuredLoan implements Loan {

}

record UnsecuredLoan(float interest) implements Loan {

}

interface CatFactClient2 {

  @Observed(name = "cats")
  @GetExchange("https://catfact.ninja/fact")
  CatFact getFact();
}

@Component
class CatFactClient {

  private final RestClient restClient;

  CatFactClient(RestClient restClient) {
    this.restClient = restClient;
  }

  CatFact fact() {
    return this.restClient
        .get()
        .uri("https://catfact.ninja/fact")
        .retrieve()
        .body(CatFact.class);
  }
}

record CatFact(String fact) {

}

@Controller
@ResponseBody
class CatFactController {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final CatFactClient catFactClient;
  private final CatFactClient2 catFactClient2;
  private final ObservationRegistry observationRegistry;

  CatFactController(CatFactClient catFactClient, CatFactClient2 catFactClient2,
      ObservationRegistry observationRegistry) {
    this.catFactClient = catFactClient;
    this.catFactClient2 = catFactClient2;
    this.observationRegistry = observationRegistry;
  }

  @GetMapping("/catfact")
  CatFact fact() {
    var fact = catFactClient.fact();
    log.info(fact.toString());
    return fact;
  }

  @GetMapping("/catfact2")
  CatFact fact2() {
    return Observation
        .createNotStarted("cats", this.observationRegistry)
        .observe(catFactClient2::getFact);
  }
}

@Controller
@ResponseBody
class CustomerController {

  private final CustomerRepository customerRepository;

  CustomerController(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @GetMapping("/customers")
  Collection<Customer> customers() {
    return customerRepository.customers();
  }
}

@Repository
class CustomerRepository {

  private final JdbcClient jdbcClient;

  CustomerRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  Collection<Customer> customers() {
    return this.jdbcClient.sql("""
            select * from customer
            """)
        .query((rs, rowNum) -> new Customer(rs.getInt("id"), rs.getString("name")))
        .list();
  }
}

record Customer(Integer id, String name) {

}
