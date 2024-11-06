package com.univille.mini_rede_social.cadastro.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.univille.mini_rede_social.cadastro.dto.input.RequestAlterarDadosCadastrais;
import com.univille.mini_rede_social.cadastro.dto.input.RequestCadastro;
import com.univille.mini_rede_social.cadastro.dto.input.RequestConfirmarEmail;
import com.univille.mini_rede_social.cadastro.dto.input.RequestReenviarEmailConfirmacao;
import com.univille.mini_rede_social.cadastro.services.CadastroService;
import com.univille.mini_rede_social.login.models.Usuario;
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
            return this.responseHandler.generateResponse(String.format("Erro ao realizar cadastro: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @PostMapping("/confirmacao/email")
    public ResponseEntity<?> confirmarEmail(@RequestBody @Valid RequestConfirmarEmail requestConfirmarEmail) {
        try {
            this.cadastroService.confirmarEmail(requestConfirmarEmail);
            return this.responseHandler.generateResponse("Email confirmado com sucesso.", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar confirmação de email: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @PostMapping("/confirmacao/reenviar")
    public ResponseEntity<?> reenviarEmailConfirmacao(@RequestBody @Valid RequestReenviarEmailConfirmacao requestReenviarEmailConfirmacao) {
        try {
            this.cadastroService.reenviarEmailConfirmacao(requestReenviarEmailConfirmacao);
            return this.responseHandler.generateResponse("Reenviado email com sucesso.", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao reenviar email de confirmação: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @PutMapping("/alterar/dados")
    public ResponseEntity<?> alterarDadosCadastrais(@RequestBody @Valid RequestAlterarDadosCadastrais requestAlterarDadosCadastrais, @AuthenticationPrincipal Usuario usuario) {
        try {
            this.cadastroService.alterarDadosCadastrais(requestAlterarDadosCadastrais, usuario);
            return this.responseHandler.generateResponse("Alterado dados com sucesso", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao alterar dados: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

}
