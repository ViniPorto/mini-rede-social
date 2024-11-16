package com.univille.mini_rede_social.amizades.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.univille.mini_rede_social.amizades.dto.input.RequestResponderSolicitacaoAmizadeDto;
import com.univille.mini_rede_social.amizades.dto.input.RequestSolicitacaoAmizadeDto;
import com.univille.mini_rede_social.amizades.services.AmizadeService;
import com.univille.mini_rede_social.amizades.services.SolicitacaoService;
import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.utils.ResponseHandler;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/amizade")
@RestController
@AllArgsConstructor
public class AmizadeController {
    
    private final ResponseHandler responseHandler;

    private final AmizadeService amizadeService;

    private final SolicitacaoService solicitacaoService;

    @GetMapping("/listar/todas")
    public ResponseEntity<?> listarTodas(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @AuthenticationPrincipal Usuario usuario) {
        try {
            var amizades = this.amizadeService.listarTodas(page, size, usuario);
            return this.responseHandler.generateResponse("Consulta realizada com sucesso", true, HttpStatus.OK, amizades);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar consulta: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/listar/sugeridos")
    public ResponseEntity<?> listarSugeridos(@AuthenticationPrincipal Usuario usuario) {
        try {
            var usuariosSugeridos = this.amizadeService.listarSugeridos(usuario);
            return this.responseHandler.generateResponse("Consulta realizada com sucesso", true, HttpStatus.OK, usuariosSugeridos);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar consulta: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @PostMapping("/solicitacao")
    public ResponseEntity<?> solicitacaoAmizade(@RequestBody @Valid RequestSolicitacaoAmizadeDto requestSolicitacaoAmizadeDto, @AuthenticationPrincipal Usuario usuario) {
        try {
            this.solicitacaoService.criarSolicitacao(requestSolicitacaoAmizadeDto.getCodigoUsuarioAdicionar(), usuario);
            return this.responseHandler.generateResponse("Enviado solicitação com sucesso", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao enviar solicitação: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/solicitacao/listar/todas")
    public ResponseEntity<?> listarTodasSolicitacoesAmizade(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @AuthenticationPrincipal Usuario usuario) {
        try {
            var solicitacoes = this.solicitacaoService.listarTodas(page, size, usuario);
            return this.responseHandler.generateResponse("Consulta realizada com sucesso", true, HttpStatus.OK, solicitacoes);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao listar solicitações: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @PostMapping("/solicitacao/resposta")
    public ResponseEntity<?> responderSolicitacao(@RequestBody @Valid RequestResponderSolicitacaoAmizadeDto requestResponderSolicitacaoAmizadeDto, @AuthenticationPrincipal Usuario usuario) {
        try {
            this.solicitacaoService.responderSolicitacao(requestResponderSolicitacaoAmizadeDto.getAceitar(), requestResponderSolicitacaoAmizadeDto.getCodigoSolicitacao(), usuario);
            return this.responseHandler.generateResponse("Solicitação processada com sucesso", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao responder solicitação: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/usuarios/listar/{nome}")
    public ResponseEntity<?> listarUsuariosPorNome(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @PathVariable String nome) {
        try {
            var usuarios = this.amizadeService.listarUsuariosPorNome(page, size, nome);
            return this.responseHandler.generateResponse("Consulta realizada com sucesso", true, HttpStatus.OK, usuarios);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao listar usuários: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> listarUsuarioPorId(@PathVariable Long id) {
        try {
            var usuario = this.amizadeService.listarUsuarioPorId(id);
            return this.responseHandler.generateResponse("Consulta realizada com sucesso", true, HttpStatus.OK, usuario);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar consulta: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/usuarios/conferir/amigo/{id}")
    public ResponseEntity<?> conferirSeEhAmigo(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        try {
            var responseConferirUsuarioAmigo = this.amizadeService.conferirSeEhAmigo(id, usuario);
            return this.responseHandler.generateResponse("Consulta realizada com sucesso", true, HttpStatus.OK, responseConferirUsuarioAmigo);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar consulta: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/usuarios/listar/amigos/{id}")
    public ResponseEntity<?> listarAmigosDeUmUsuario(@PathVariable Long id) {
        try {
            var amigos = this.amizadeService.listarAmigosDeUmUsuario(id);
            return this.responseHandler.generateResponse("Consulta realizada com sucesso", true, HttpStatus.OK, amigos);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar consulta: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

}
