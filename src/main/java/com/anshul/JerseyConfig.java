package com.anshul;

import com.anshul.controller.StatisticsController;
import com.anshul.controller.TransactionController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
@javax.ws.rs.ApplicationPath("api")
public class JerseyConfig extends ResourceConfig {
  public JerseyConfig() {
    register(TransactionController.class);
    register(StatisticsController.class);
  }
}
