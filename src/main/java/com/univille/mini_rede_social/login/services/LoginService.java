package com.univille.mini_rede_social.login.services;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.univille.mini_rede_social.infra.security.services.TokenService;
import com.univille.mini_rede_social.login.dto.output.ResponseLoginDto;
import com.univille.mini_rede_social.login.exceptions.EmailNaoConfirmadoException;
import com.univille.mini_rede_social.login.models.Usuario;

import lombok.AllArgsConstructor;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@AllArgsConstructor
public class LoginService {
    
    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    public ResponseLoginDto autenticar(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws EmailNaoConfirmadoException {
        var authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        var usuario = (Usuario) authentication.getPrincipal();
        
        if(!usuario.isEmailConfirmado()) {
            throw new EmailNaoConfirmadoException("Email não foi confirmado!");
        }

        var tokenJWT = tokenService.gerarToken(usuario);
        return new ResponseLoginDto(tokenJWT);
    }
}
