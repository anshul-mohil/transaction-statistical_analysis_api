package com.anshul.controller;

import com.anshul.model.StatisticsResource;
import com.anshul.service.IStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.Produces;

@Controller
public class StatisticsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);

  @Autowired
  private IStatisticsService statisticsService;

  @Produces("application/json")
  @RequestMapping(value = "/statistics", method = RequestMethod.GET)
  public ResponseEntity<StatisticsResource> getStatistics() {
    LOGGER.debug("========= Inside getStatistics() ============");
    return new ResponseEntity<>(statisticsService.getStatistics(), HttpStatus.OK);
  }
}
