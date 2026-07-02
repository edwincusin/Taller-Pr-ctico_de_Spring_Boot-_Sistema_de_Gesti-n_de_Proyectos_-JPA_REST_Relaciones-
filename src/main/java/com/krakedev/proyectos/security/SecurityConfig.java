package com.krakedev.proyectos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	// Variable que almacenará una instancia de tu filtro JWT
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}
	
	// Indica a Spring que el objeto retornado será un Bean administrado
	@Bean
	// Método que configura toda la seguridad de la aplicación
	public SecurityFilterChain  cadenaFiltrosSeguridad(HttpSecurity http) {
		
		return http
				.csrf(csrf->csrf.disable())
				.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth-> auth 
						.requestMatchers("/api/auth/login", "/api/auth/registrar","/api/proyectos/publico/resumen")
						.permitAll()
						.anyRequest()
						.authenticated())
	            // Agrega tu filtro JWT antes del filtro de autenticación de Spring Security

				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build(); // Construye y devuelve la cadena de filtros de seguridad

	}

}
