package com.univille.mini_rede_social.infra.security.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.univille.mini_rede_social.infra.AppConfigurations;
import com.univille.mini_rede_social.infra.security.exceptions.TokenJWTInvalidoOuExpiradoException;
import com.univille.mini_rede_social.login.models.Usuario;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Scope("prototype")
public class TokenService {
    
    @Autowired
    private final AppConfigurations appConfigurations;

    public String gerarToken(Usuario usuario){
        try {
            var algoritmo = Algorithm.HMAC256(appConfigurations.getTokenSecret());
            return JWT.create()
                .withIssuer("Mini-rede-social")
                .withSubject(usuario.getEmail())
                .withExpiresAt(dataExpiracao())
                .sign(algoritmo);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar Token JWT", exception);
        }
    }

    public String getSubject(String tokenJWT) throws TokenJWTInvalidoOuExpiradoException{
        try {
            var algoritmo = Algorithm.HMAC256(appConfigurations.getTokenSecret());
            return JWT.require(algoritmo)
                .withIssuer("HealthLab API")
                .build()
                .verify(tokenJWT)
                .getSubject();
        } catch (JWTVerificationException exception){
            throw new TokenJWTInvalidoOuExpiradoException("Token JWT inválido ou expirado!");
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
