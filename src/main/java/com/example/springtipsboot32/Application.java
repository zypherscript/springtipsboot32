package com.example.springtipsboot32;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
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
