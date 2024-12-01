package com.univille.mini_rede_social.notificacao.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.notificacao.dto.output.ResponseNotificacaoDto;
import com.univille.mini_rede_social.notificacao.exceptions.NotificacaoNaoEncontradaException;
import com.univille.mini_rede_social.notificacao.services.NotificacaoService;
import com.univille.mini_rede_social.utils.ResponseHandler;

@ExtendWith(MockitoExtension.class)
class NotificacaoControllerTest {
    
    @Mock
    NotificacaoService notificacaoService;

    NotificacaoController notificacaoController;

    @BeforeEach
    void onBefore() {
        this.notificacaoController = new NotificacaoController(new ResponseHandler(), notificacaoService);
    }

    @Test
    void deveRetornarOkQuandoListarNovasNotificacoesCorretamente() {
        @SuppressWarnings("unchecked")
        Page<ResponseNotificacaoDto> pageMock = mock(Page.class);

        when(this.notificacaoService.listarNovas(anyInt(), anyInt(), any(Usuario.class))).thenReturn(pageMock);

        var response = this.notificacaoController.listarNovas(0, 10, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoListarNovasNotificacoes() {
        when(this.notificacaoService.listarNovas(anyInt(), anyInt(), any(Usuario.class))).thenThrow(new RuntimeException("Erro inesperado"));

        var response = this.notificacaoController.listarNovas(0, 10, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deveRetornarOkQuandoListarTodasAsNotificacoesCorretamente() {
        @SuppressWarnings("unchecked")
        Page<ResponseNotificacaoDto> pageMock = mock(Page.class);

        when(this.notificacaoService.listarTodas(anyInt(), anyInt(), any(Usuario.class))).thenReturn(pageMock);

        var response = this.notificacaoController.listarTodas(0, 10, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoListarTodasAsNotificacoes() {
        when(this.notificacaoService.listarTodas(anyInt(), anyInt(), any(Usuario.class))).thenThrow(new RuntimeException("Erro inesperado"));

        var response = this.notificacaoController.listarTodas(0, 10, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deveRetornarOkQuandoConfirmarLeituraCorretamente() throws NotificacaoNaoEncontradaException {
        doNothing().when(this.notificacaoService).confirmarLeitura(anyLong(), any(Usuario.class));

        var response = this.notificacaoController.confirmarLeitura(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarBadRequestQuandoOcorrerNotificacaoNaoEncontradaExceptionAoConfirmarLeitura() throws NotificacaoNaoEncontradaException {
        doThrow(NotificacaoNaoEncontradaException.class).when(this.notificacaoService).confirmarLeitura(anyLong(), any(Usuario.class));

        var response = this.notificacaoController.confirmarLeitura(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoConfirmarLeitura() throws NotificacaoNaoEncontradaException {
        doThrow(RuntimeException.class).when(this.notificacaoService).confirmarLeitura(anyLong(), any(Usuario.class));

        var response = this.notificacaoController.confirmarLeitura(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

}
