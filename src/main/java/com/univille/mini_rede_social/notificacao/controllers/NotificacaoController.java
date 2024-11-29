package com.univille.mini_rede_social.notificacao.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.notificacao.exceptions.NotificacaoNaoEncontradaException;
import com.univille.mini_rede_social.notificacao.services.NotificacaoService;
import com.univille.mini_rede_social.utils.ResponseHandler;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/notificacao")
@AllArgsConstructor
public class NotificacaoController {
    
    private final ResponseHandler responseHandler;

    private final NotificacaoService notificacaoService;

    @GetMapping("/listar/novas")
    public ResponseEntity<?> listarNovas(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @AuthenticationPrincipal Usuario usuario) {
        try {
            var notificacoes = this.notificacaoService.listarNovas(page, size, usuario);
            return this.responseHandler.generateResponse("Consulta realizada com sucesso", true, HttpStatus.OK, notificacoes);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar consulta: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping("/listar/todas")
    public ResponseEntity<?> listarTodas(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @AuthenticationPrincipal Usuario usuario) {
        try {
            var notificacoes = this.notificacaoService.listarTodas(page, size, usuario);
            return this.responseHandler.generateResponse("Consulta realizada com sucesso", true, HttpStatus.OK, notificacoes);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar consulta: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @PostMapping("/confirmar-leitura")
    public ResponseEntity<?> confirmarLeitura(@RequestParam Long codigo, @AuthenticationPrincipal Usuario usuario) {
        try {
            this.notificacaoService.confirmarLeitura(codigo, usuario);
            return this.responseHandler.generateResponse("Confirmado leitura com sucesso", true, HttpStatus.OK, null);
        } catch (NotificacaoNaoEncontradaException e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar confirmar leitura: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Erro ao realizar confirmar leitura: %s", e.getMessage()), false, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

}
