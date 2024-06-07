package accesskey.access.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/home","/auth/login", "/auth/signup","/auth/forgotpassword", "/auth/reset-password", "/auth/password/resets/", "/api/users/password/reset", "/api/users/password/reset/confirm").permitAll() // Allow access to login and signup pages
                        .requestMatchers("/api/users/createAccessKey").hasRole("SCHOOL_IT")
                        .requestMatchers("/api/users/myAccessKeys").hasRole("SCHOOL_IT")
                        .requestMatchers("/user/**").hasRole("SCHOOL_IT")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/accesskeys/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler((request, response, authentication) -> {
                            for (GrantedAuthority authority : authentication.getAuthorities()) {
                                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                                    response.sendRedirect("/admin");
                                    return;
                                }
                            }
                            response.sendRedirect("/user");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
