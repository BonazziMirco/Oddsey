package unitn.bonazzi.oddssey.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import javax.sql.DataSource;


@Configuration
@ComponentScan("unitn.bonazzi.oddssey")
public class SecurityConfig {

    // User details manager
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    // Password encoder
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security chain
    @Bean
    public SecurityFilterChain configure(HttpSecurity http)
            throws Exception {


        // Authentication
        http.formLogin(c ->
                c.loginPage("/login")
                        .defaultSuccessUrl("/dashboard")
                        .failureForwardUrl("/loginFailure") //Pay attention it is a forward! So, in the Controller you should use @PostMapping
        );


        // Authorization
        http.authorizeHttpRequests(c ->
                c.requestMatchers("/dashboard").hasAnyRole("ADMIN", "USER", "MODERATOR")
                        .requestMatchers("/userDashboard").hasAnyRole("USER", "MODERATOR")
                        .requestMatchers("/userDetails").hasAnyRole("USER", "MODERATOR")
                        .requestMatchers("/changePassword").hasAnyRole("USER", "MODERATOR")
                        .requestMatchers("/matchCalendar").hasAnyRole("USER", "MODERATOR")
                        .requestMatchers("/wager").hasAnyRole("USER", "MODERATOR")
                        .requestMatchers("/review").hasAnyRole("USER", "MODERATOR")
                        .requestMatchers("/publishReview").hasAnyRole("USER", "MODERATOR")
                        .requestMatchers("/wrongPassword").hasAnyRole("USER", "MODERATOR")
                        .requestMatchers("/adminDashboard").hasRole("ADMIN")
                        .requestMatchers("/userList").hasRole("ADMIN")
                        .requestMatchers("/rankingList").hasRole("ADMIN")
                        .requestMatchers("/assegnaPremi").hasRole("ADMIN")
                        .requestMatchers("/upgradeUser").hasRole("ADMIN")
                        .anyRequest().permitAll()
        );


        // Logout
        http.logout(c ->
                c.logoutUrl("/logout") // It is the Spring Security logout endpoint
                        .logoutSuccessUrl("/index") //Pay attention it is using a GET-redirect under the hood! So, in the Controller you should use @GetMapping


        );


        // TO DISABLE CSRF PROTECTION
        http.csrf(AbstractHttpConfigurer::disable);


        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            UserDetailsManager userDetailsManager,
            PasswordEncoder passwordEncoder
    ) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsManager)
                .passwordEncoder(passwordEncoder);
        return builder.build();
    }
}

