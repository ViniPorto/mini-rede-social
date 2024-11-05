package com.univille.mini_rede_social.cadastro.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univille.mini_rede_social.cadastro.dto.input.RequestCadastro;
import com.univille.mini_rede_social.cadastro.services.CadastroService;
import com.univille.mini_rede_social.utils.ResponseHandler;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/cadastro")
@RestController
@AllArgsConstructor
public class CadastroController {

    private final ResponseHandler responseHandler;

    private final CadastroService cadastroService;
    
    @PostMapping("/usuario")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody @Valid RequestCadastro requestCadastro) {
        try {
            this.cadastroService.cadastrar(requestCadastro);
            return this.responseHandler.generateResponse("Usuário cadastrado com sucesso. Verifique a caixa de email para obter o código de confirmação.", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return responseHandler.generateResponse(String.format("Erro ao realizar cadastro: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

}
