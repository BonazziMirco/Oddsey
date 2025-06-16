package unitn.bonazzi.oddssey.configurations;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "unitn.bonazzi.oddssey")
public class OpenFeignConfig {

}