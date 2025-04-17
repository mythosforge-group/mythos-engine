package mythosforge.fable_minds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desative apenas para testes iniciais
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/login", "/api/users/**").permitAll() // Libere as rotas de usuário
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/api/home")
            );

        return http.build();
    }

    // Configuração para buscar usuários do PostgreSQL
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
    
        // Query personalizada para buscar usuário
        userDetailsManager.setUsersByUsernameQuery(
            "SELECT username, password, enabled FROM users WHERE username=?"
        );
        
        // Query personalizada para buscar roles
        userDetailsManager.setAuthoritiesByUsernameQuery(
            "SELECT u.username, a.authority FROM users u " +
            "JOIN authorities a ON u.id = a.user_id " +
            "WHERE u.username=?"
        );
        
        return userDetailsManager;
    }

    // Codificador de senhas BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}