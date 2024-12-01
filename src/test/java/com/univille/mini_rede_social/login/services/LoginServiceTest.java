package com.univille.mini_rede_social.login.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.univille.mini_rede_social.infra.security.services.TokenService;
import com.univille.mini_rede_social.login.exceptions.EmailNaoConfirmadoException;
import com.univille.mini_rede_social.login.models.Usuario;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    
    @Mock
    TokenService tokenService;

    @Mock
    AuthenticationManager authenticationManager;

    LoginService loginService;

    @BeforeEach
    void onBefore() {
        this.loginService = new LoginService(tokenService, authenticationManager);
    }

    @Test
    void deveLancarEmailNaoConfirmadoExceptionAoAutenticar() {
        var usuario = new Usuario();
        usuario.setEmailConfirmado(Boolean.FALSE);

        var authentication = mock(Authentication.class); 
        
        when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);

        Assertions.assertThrows(EmailNaoConfirmadoException.class, () -> {
            this.loginService.autenticar(new UsernamePasswordAuthenticationToken("", ""));
        });
    }

    @Test
    void deveAutenticarCorretamente() throws EmailNaoConfirmadoException {
        var usuario = new Usuario();
        usuario.setEmailConfirmado(Boolean.TRUE);

        var authentication = mock(Authentication.class); 
        
        when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);

        when(this.tokenService.gerarToken(any(Usuario.class))).thenReturn("token");

        var response = this.loginService.autenticar(new UsernamePasswordAuthenticationToken("", ""));

        Assertions.assertNotNull(response);
    }

}
