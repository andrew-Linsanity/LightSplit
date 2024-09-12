package com.LightSplit.demo.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; 
import org.springframework.http.HttpMethod; 
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // private CustomUserDetailsService userDetailService;  

    @Autowired
    private JWTAuthEntryPoint authEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable()) // Disable CSRF protection if not needed
        .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authEntryPoint)) // Ensure authEntryPoint is defined
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(httpBasic -> httpBasic.disable()); // Disable HTTP Basic if using JWT

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    } 

    @Bean 
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
    }

    // password encoder deleted

    @Bean 
    public JWTAuthenticationFilter jwtAuthenticationFilter() { 
        return new JWTAuthenticationFilter();
    }
}
