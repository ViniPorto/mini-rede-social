package com.univille.mini_rede_social.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableWebMvc
public class SecurityConfiguration {
    
    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/login", 
                                                "/cadastro/usuario",
                                                "/cadastro/confirmacao/email",
                                                "/cadastro/confirmacao/reenviar",
                                                "/cadastro/solicitacao/troca/senha",
                                                "/cadastro/confirmacao/troca/senha").permitAll()
                .anyRequest().authenticated()
                .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
