package com.univille.mini_rede_social.login.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univille.mini_rede_social.login.dto.input.RequestLoginDto;
import com.univille.mini_rede_social.login.exceptions.EmailNaoConfirmadoException;
import com.univille.mini_rede_social.login.services.LoginService;
import com.univille.mini_rede_social.utils.ResponseHandler;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {
    
    private final ResponseHandler responseHandler;

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid RequestLoginDto requestLoginDto) {

        try {
            var responseLoginDto = loginService.autenticar(new UsernamePasswordAuthenticationToken(requestLoginDto.getEmail(), requestLoginDto.getSenha()));
            return responseHandler.generateResponse("Autenticado com sucesso", true, HttpStatus.OK, responseLoginDto);
        } catch (EmailNaoConfirmadoException e) {
            return responseHandler.generateResponse(String.format("Erro ao autenticar: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            return responseHandler.generateResponse(String.format("Erro ao autenticar: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }

    }
    
}
