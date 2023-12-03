package com.example.springtipsboot32;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
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
