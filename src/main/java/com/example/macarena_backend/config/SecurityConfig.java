package com.example.macarena_backend.config;

import com.example.macarena_backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            	    .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll()
            	    .requestMatchers("/error").permitAll()
            	    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            	    // ... rest unchanged

                // Public
                .requestMatchers(
                        "/api/auth/**",
                        "/api/customers/register",
                        "/uploads/**",
                        "/api/products/**",
                        "/api/sizes/**",
                        "/api/dress-types/**"
                ).permitAll()

                // Customer-only
                .requestMatchers("/api/cart/**").hasRole("CUSTOMER")
                .requestMatchers("/api/profile/**").hasRole("CUSTOMER")
                .requestMatchers("/api/payments/**").hasRole("CUSTOMER")
                .requestMatchers("/api/likes/**").hasRole("CUSTOMER")   // 👈 ADDED
                .requestMatchers("/api/couriers/**").permitAll()
                // Orders — customers create/view their own; admins view/update
                .requestMatchers("/api/orders/**").hasAnyRole("CUSTOMER", "ADMIN")

                // Admin-only
                .requestMatchers("/api/admins/**").hasRole("ADMIN")
                .requestMatchers("/api/admin-profile/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:4200"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}