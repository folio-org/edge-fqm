package org.folio.fqm.edge;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.security.Security;

import static org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider.PROVIDER_NAME;

@SpringBootApplication
@EnableFeignClients
public class EdgeFqmApplication {

  public static void main(String[] args) {
    if (Security.getProvider(PROVIDER_NAME) == null) {
      Security.addProvider(new BouncyCastleFipsProvider());
    }
    SpringApplication.run(EdgeFqmApplication.class, args);
  }

}
