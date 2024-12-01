package com.univille.mini_rede_social.login.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.univille.mini_rede_social.login.dto.input.RequestLoginDto;
import com.univille.mini_rede_social.login.dto.output.ResponseLoginDto;
import com.univille.mini_rede_social.login.exceptions.EmailNaoConfirmadoException;
import com.univille.mini_rede_social.login.services.LoginService;
import com.univille.mini_rede_social.utils.ResponseHandler;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {
    
    @Mock
    LoginService loginService;

    LoginController loginController;

    @BeforeEach
    void onBefore() {
        this.loginController = new LoginController(new ResponseHandler(), loginService);
    }

    @Test
    void deveRetornarOkQuandoEfetuarLoginCorretamente() throws EmailNaoConfirmadoException {
        var responseLoginDto = new ResponseLoginDto("");
        
        when(this.loginService.autenticar(any(UsernamePasswordAuthenticationToken.class))).thenReturn(responseLoginDto);

        var requestLoginDto = new RequestLoginDto();
        requestLoginDto.setEmail("email");
        requestLoginDto.setSenha("senha");

        var response = this.loginController.efetuarLogin(requestLoginDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarBadRequestQuandoOcorrerEmailNaoConfirmadoExceptionAoEfetuarLogin() throws EmailNaoConfirmadoException {
        when(this.loginService.autenticar(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new EmailNaoConfirmadoException("Erro conhecido"));

        var requestLoginDto = new RequestLoginDto();
        requestLoginDto.setEmail("email");
        requestLoginDto.setSenha("senha");

        var response = this.loginController.efetuarLogin(requestLoginDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoEfetuarLogin() throws EmailNaoConfirmadoException {
        when(this.loginService.autenticar(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Erro conhecido"));

        var requestLoginDto = new RequestLoginDto();
        requestLoginDto.setEmail("email");
        requestLoginDto.setSenha("senha");

        var response = this.loginController.efetuarLogin(requestLoginDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

}
