package com.humannative.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

	private final JWTFilter jwtFilter;

	private final LocalAuthenticationProvider authenticationProvider;

	public WebSecurityConfig(LocalAuthenticationProvider authenticationProvider, JWTFilter jwtFilter) {
		this.authenticationProvider = authenticationProvider;
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests((authorizeHttpRequests) -> {
				authorizeHttpRequests
					.requestMatchers("/public/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
					.permitAll()
					.anyRequest()
					.fullyAuthenticated();
			});
		http.authenticationManager(new ProviderManager(List.of(authenticationProvider)));
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
