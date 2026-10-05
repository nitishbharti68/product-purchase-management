package com.nitish.product_purchase_management.config;

import com.nitish.product_purchase_management.security.CustomAccessDeniedHandler;
import com.nitish.product_purchase_management.security.JwtAuthenticationEntryPoint;
import com.nitish.product_purchase_management.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        private final UserDetailsService userDetailsService;

        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        private final CustomAccessDeniedHandler customAccessDeniedHandler;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        UserDetailsService userDetailsService,
                        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                        CustomAccessDeniedHandler customAccessDeniedHandler) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;

                this.userDetailsService = userDetailsService;

                this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;

                this.customAccessDeniedHandler = customAccessDeniedHandler;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {

                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(
                                userDetailsService);

                provider.setPasswordEncoder(
                                passwordEncoder());

                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {

                return configuration
                                .getAuthenticationManager();
        }

        @Bean
        public UrlBasedCorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                List.of(
                                                "http://localhost:4200"));

                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "PATCH",
                                                "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of(
                                                "Authorization",
                                                "Content-Type",
                                                "Accept"));

                configuration.setExposedHeaders(
                                List.of(
                                                "Authorization"));

                /*
                 * The application uses a Bearer token in the
                 * Authorization header instead of authentication cookies.
                 */
                configuration.setAllowCredentials(false);

                /*
                 * Cache the preflight result for one hour.
                 */
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                jwtAuthenticationEntryPoint)
                                                .accessDeniedHandler(
                                                                customAccessDeniedHandler))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                HttpMethod.OPTIONS,
                                                                "/**")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/api/v1/auth/register",
                                                                "/api/v1/auth/login")
                                                .permitAll()

                                                // ---- Swagger / OpenAPI (NEW) ----
                                                .requestMatchers(
                                                                "/v3/api-docs",
                                                                "/v3/api-docs/**",
                                                                "/v3/api-docs.yaml",
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/api/v1/admin/**")
                                                .hasRole("ADMIN")

                                                .anyRequest()
                                                .authenticated())

                                .authenticationProvider(
                                                authenticationProvider())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}