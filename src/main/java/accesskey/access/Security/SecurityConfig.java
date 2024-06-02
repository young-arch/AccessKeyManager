package accesskey.access.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/users/password/reset", "/api/users/password/reset/confirm").permitAll()
                        .requestMatchers("api/users/createAccessKey").hasRole("SCHOOL_IT")
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/accesskeys/**").hasRole("ADMIN")
                        .anyRequest().authenticated()

                )
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .logout(LogoutConfigurer::permitAll)
                .httpBasic(Customizer.withDefaults());
        return http.build();

    }


    @Bean
    //Password Encoder for encoding passwords
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();

    }

}