package unitn.bonazzi.oddssey.configurations;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan("unitn.bonazzi.oddssey")
@EnableFeignClients(basePackages = "unitn.bonazzi.oddssey")
public class OpenFeignConfig {

}