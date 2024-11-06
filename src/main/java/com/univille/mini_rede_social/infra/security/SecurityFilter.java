package com.univille.mini_rede_social.infra.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.univille.mini_rede_social.infra.security.services.TokenService;
import com.univille.mini_rede_social.login.repositories.UsuarioRepository;
import com.univille.mini_rede_social.utils.ResponseHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final UsuarioRepository usuarioRepository;

    private final ResponseHandler responseHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var tokenJWT = recuperarToken(request);

            if(tokenJWT != null){
                var subject = tokenService.getSubject(tokenJWT);
                var usuario = usuarioRepository.findByEmail(subject).get();

                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) { //Caso o Token esteja expirado ou inválido

            var map = responseHandler.generateMap(e.getMessage(), false, HttpStatus.FORBIDDEN, null);
            var mapper = new ObjectMapper();
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(mapper.writeValueAsString(map));
            
        }
        
    }

    public static String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null){
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

}
