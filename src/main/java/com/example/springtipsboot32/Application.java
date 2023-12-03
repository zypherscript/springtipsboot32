package com.example.springtipsboot32;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
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

