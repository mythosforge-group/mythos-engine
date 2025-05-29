package mythosforge.fable_minds.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import mythosforge.fable_minds.config.security.auhentication.filter.JwtAuthenticationFilter;
import mythosforge.fable_minds.config.security.auhentication.service.CustomUserDetailsService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // necessário pro H2 funcionar
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll() // Permite POST no login
                        .requestMatchers(HttpMethod.OPTIONS, "/auth/**").permitAll() // Permite OPTIONS para CORS
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()
                        .requestMatchers("/api/personagens/**").authenticated()
                        .requestMatchers("/api/personagens/dnd/**").authenticated()
                        .requestMatchers("/api/personagens/dnd/npc/**").authenticated()
                        .requestMatchers("/api/races/**").authenticated()
                        .requestMatchers("/api/systems/**").authenticated()
                        .requestMatchers("/api/classes/**").authenticated()
                        .requestMatchers("/api/campaigns/**").authenticated()
                        .requestMatchers("/v3/**").permitAll() // Libera Swagger UI
                        .requestMatchers("/swagger-ui/**").permitAll() // Libera Swagger UI
                        .requestMatchers("/h2-console/**").permitAll() // Libera h2-console
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // return NoOpPasswordEncoder.getInstance(); // Não use em produção! Apenas para testes.
        return new BCryptPasswordEncoder();
    }
}