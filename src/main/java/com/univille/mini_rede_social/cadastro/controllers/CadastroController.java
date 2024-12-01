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
import com.univille.mini_rede_social.cadastro.dto.input.RequestConfirmarTrocaSenha;
import com.univille.mini_rede_social.cadastro.dto.input.RequestReenviarEmailConfirmacao;
import com.univille.mini_rede_social.cadastro.dto.input.RequestSolicitarTrocaSenha;
import com.univille.mini_rede_social.cadastro.exceptions.CodigoConfirmacaoExpiradoException;
import com.univille.mini_rede_social.cadastro.exceptions.ConfirmacaoNaoEncontradaException;
import com.univille.mini_rede_social.cadastro.exceptions.EmailJaConfirmadoException;
import com.univille.mini_rede_social.cadastro.exceptions.UsuarioJaCadastradoException;
import com.univille.mini_rede_social.cadastro.exceptions.UsuarioNaoCadastradoException;
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
            return this.responseHandler.generateResponse("Usuário cadastrado com sucesso. Verifique a caixa de email para obter o código de confirmação.", true, HttpStatus.CREATED, null);
        } catch (UsuarioJaCadastradoException e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar cadastro: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar cadastro: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/confirmacao/email")
    public ResponseEntity<?> confirmarEmail(@RequestBody @Valid RequestConfirmarEmail requestConfirmarEmail) {
        try {
            this.cadastroService.confirmarEmail(requestConfirmarEmail);
            return this.responseHandler.generateResponse("Email confirmado com sucesso.", true, HttpStatus.OK, null);
        } catch (UsuarioNaoCadastradoException | EmailJaConfirmadoException | ConfirmacaoNaoEncontradaException | CodigoConfirmacaoExpiradoException e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar confirmação de email: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar confirmação de email: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/confirmacao/reenviar")
    public ResponseEntity<?> reenviarEmailConfirmacao(@RequestBody @Valid RequestReenviarEmailConfirmacao requestReenviarEmailConfirmacao) {
        try {
            this.cadastroService.reenviarEmailConfirmacao(requestReenviarEmailConfirmacao);
            return this.responseHandler.generateResponse("Reenviado email com sucesso.", true, HttpStatus.OK, null);
        } catch (UsuarioNaoCadastradoException | EmailJaConfirmadoException e) {
            return this.responseHandler.generateResponse(String.format("Erro ao reenviar email de confirmação: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao reenviar email de confirmação: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PutMapping("/alterar/dados")
    public ResponseEntity<?> alterarDadosCadastrais(@RequestBody @Valid RequestAlterarDadosCadastrais requestAlterarDadosCadastrais, @AuthenticationPrincipal Usuario usuario) {
        try {
            this.cadastroService.alterarDadosCadastrais(requestAlterarDadosCadastrais, usuario);
            return this.responseHandler.generateResponse("Alterado dados com sucesso", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao alterar dados: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/solicitacao/troca/senha")
    public ResponseEntity<?> solicitarTrocaSenha(@RequestBody @Valid RequestSolicitarTrocaSenha requestSolicitarTrocaSenha) {
        try {
            this.cadastroService.solicitarTrocaSenha(requestSolicitarTrocaSenha);
            return this.responseHandler.generateResponse("Solicitado troca de senha com sucesso. Verifique a caixa de email.", true, HttpStatus.OK, null);
        } catch (UsuarioNaoCadastradoException e) {
            return this.responseHandler.generateResponse(String.format("Erro ao solicitar troca de senha: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao solicitar troca de senha: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/confirmacao/troca/senha")
    public ResponseEntity<?> confirmarTrocaSenha(@RequestBody @Valid RequestConfirmarTrocaSenha requestConfirmarTrocaSenha) {
        try {
            this.cadastroService.confirmarTrocaSenha(requestConfirmarTrocaSenha);
            return this.responseHandler.generateResponse("Senha alterada com sucesso.", true, HttpStatus.OK, null);
        } catch (UsuarioNaoCadastradoException | ConfirmacaoNaoEncontradaException | CodigoConfirmacaoExpiradoException e) {
            return this.responseHandler.generateResponse(String.format("Erro ao confirmar troca de senha: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao confirmar troca de senha: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

}
