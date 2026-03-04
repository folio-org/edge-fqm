package org.folio.fqm.edge;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

import static org.folio.common.utils.tls.FipsChecker.getFipsChecksResultString;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Log4j2
public class EdgeFqmApplication {

  public static void main(String[] args) {
    log.info(getFipsChecksResultString());
    SpringApplication.run(EdgeFqmApplication.class, args);
  }

}
