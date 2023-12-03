package com.example.springtipsboot32;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestApplication {

  @Bean
  @ServiceConnection
  @RestartScope
  PostgreSQLContainer<?> postgresContainer() {
    System.out.println("Hello");
    return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
  }

  public static void main(String[] args) {
    SpringApplication.from(Application::main).with(TestApplication.class).run(args);
  }

}

