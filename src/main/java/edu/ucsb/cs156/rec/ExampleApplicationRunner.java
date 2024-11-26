package edu.ucsb.cs156.rec;

import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.services.RequestTypeService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

/** This class contains a `run` method that is called once at application startup time. */
@Slf4j
@Configuration
public class ExampleApplicationRunner implements ApplicationRunner {
  @Autowired RequestTypeService requestTypeService;

  /**
   * Called once at application startup time. Put code here if you want it to run once each time the
   * Spring Boot application starts up.
   */
  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("ExampleApplicationRunner.run called");

    String[] beginningTypes = new String[] {"CS Department BS/MS program",
    "Scholarship or Fellowship",
    "MS program (other than CS Dept BS/MS)",
    "PhD program",
    "Other"};

    List<RequestType> requestTypes = new ArrayList<RequestType>();

    for (String type : beginningTypes) {
      RequestType saving = new RequestType();
      saving.setRequestType(type);
      requestTypes.add(saving);
    }

    requestTypeService.trySaveTypes(requestTypes);
  }
}