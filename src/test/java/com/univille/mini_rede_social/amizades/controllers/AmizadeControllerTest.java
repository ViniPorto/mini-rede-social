package com.univille.mini_rede_social.amizades.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import com.univille.mini_rede_social.amizades.dto.input.RequestResponderSolicitacaoAmizadeDto;
import com.univille.mini_rede_social.amizades.dto.output.ResponseConferirUsuarioAmigoDto;
import com.univille.mini_rede_social.amizades.dto.output.ResponseSolicitacaoDto;
import com.univille.mini_rede_social.amizades.exceptions.AmizadeJaExistenteException;
import com.univille.mini_rede_social.amizades.exceptions.AmizadeNaoEncontradaException;
import com.univille.mini_rede_social.amizades.exceptions.SolicitacaoJaEnviadaExcetion;
import com.univille.mini_rede_social.amizades.exceptions.SolicitacaoNaoCadastradaException;
import com.univille.mini_rede_social.amizades.exceptions.UsuarioRepetidoException;
import com.univille.mini_rede_social.amizades.services.AmizadeService;
import com.univille.mini_rede_social.amizades.services.SolicitacaoService;
import com.univille.mini_rede_social.cadastro.exceptions.UsuarioNaoCadastradoException;
import com.univille.mini_rede_social.login.dto.output.ResponseUsuarioDto;
import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.utils.ResponseHandler;

@ExtendWith(MockitoExtension.class)
class AmizadeControllerTest {
    
    @Mock
    AmizadeService amizadeService;

    @Mock
    SolicitacaoService solicitacaoService;

    AmizadeController amizadeController;

    @BeforeEach
    void onBefore() {
        this.amizadeController = new AmizadeController(new ResponseHandler(), amizadeService, solicitacaoService);
    }

    @Test
    void deveRetornarOkQuandoListarTodasAmizadesCorretamente() {
        @SuppressWarnings("unchecked")
        Page<ResponseUsuarioDto> pageMock = mock(Page.class);
        when(this.amizadeService.listarTodas(anyInt(), anyInt(), any(Usuario.class))).thenReturn(pageMock);

        var response = this.amizadeController.listarTodas(0, 10, new Usuario());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Consulta realizada com sucesso"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoAoListarTodasAmizades() {
        when(this.amizadeService.listarTodas(anyInt(), anyInt(), any(Usuario.class))).thenThrow(new RuntimeException("Erro inesperado"));

        var response = this.amizadeController.listarTodas(0, 10, new Usuario());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao realizar consulta"));
    }

    @Test
    void deveRetornarOkQuandoListarPessoasSugeridasCorretamente() {
        List<ResponseUsuarioDto> sugeridos = new ArrayList<>();

        when(this.amizadeService.listarSugeridos(any(Usuario.class))).thenReturn(sugeridos);

        var response = this.amizadeController.listarSugeridos(new Usuario());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Consulta realizada com sucesso"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoAoListarPessoasSugeridas() {
        when(this.amizadeService.listarSugeridos(any(Usuario.class))).thenThrow(new RuntimeException("Erro inesperado"));

        var response = this.amizadeController.listarSugeridos(new Usuario());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao realizar consulta"));
    }

    @Test
    void deveRetornarOkQuandoEnviarSolicitacaoDeAmizadeCorretamente() throws UsuarioNaoCadastradoException, UsuarioRepetidoException, SolicitacaoJaEnviadaExcetion, AmizadeJaExistenteException {
        doNothing().when(this.solicitacaoService).criarSolicitacao(anyLong(), any(Usuario.class));

        var response = this.amizadeController.solicitacaoAmizade(1L, new Usuario());

        verify(this.solicitacaoService, times(1)).criarSolicitacao(anyLong(), any(Usuario.class));
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Enviado solicitação com sucesso"));
    }

    @Test
    void deveRetornarBadRequestQuandoOcorrerExcecaoConhecidaAoEnviarSolicitacaoDeAmizade() throws UsuarioNaoCadastradoException, UsuarioRepetidoException, SolicitacaoJaEnviadaExcetion, AmizadeJaExistenteException {
        doThrow(new UsuarioNaoCadastradoException("Erro conhecido")).when(this.solicitacaoService).criarSolicitacao(anyLong(), any(Usuario.class));

        var response = this.amizadeController.solicitacaoAmizade(1L, new Usuario());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro conhecido"));

        doThrow(new UsuarioRepetidoException("Erro conhecido")).when(this.solicitacaoService).criarSolicitacao(anyLong(), any(Usuario.class));

        response = this.amizadeController.solicitacaoAmizade(1L, new Usuario());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro conhecido"));

        doThrow(new SolicitacaoJaEnviadaExcetion("Erro conhecido")).when(this.solicitacaoService).criarSolicitacao(anyLong(), any(Usuario.class));

        response = this.amizadeController.solicitacaoAmizade(1L, new Usuario());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro conhecido"));

        doThrow(new AmizadeJaExistenteException("Erro conhecido")).when(this.solicitacaoService).criarSolicitacao(anyLong(), any(Usuario.class));

        response = this.amizadeController.solicitacaoAmizade(1L, new Usuario());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro conhecido"));
    }

    @Test
    void deveRetornarOkQuandoListarTodasAsSolicitacoesDeAmizadeCorretamente() {
        @SuppressWarnings("unchecked")
        Page<ResponseSolicitacaoDto> pageMock = mock(Page.class);

        when(this.solicitacaoService.listarTodas(anyInt(), anyInt(), any(Usuario.class))).thenReturn(pageMock);

        var response = this.amizadeController.listarTodasSolicitacoesAmizade(0, 10, new Usuario());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Consulta realizada com sucesso"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoAoListarTodasSolicitacoesDeAmizade() {
        when(this.solicitacaoService.listarTodas(anyInt(), anyInt(), any(Usuario.class))).thenThrow(new RuntimeException("Erro inesperado"));

        var response = this.amizadeController.listarTodasSolicitacoesAmizade(0, 10, new Usuario());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro inesperado"));
    }

    @Test
    void deveRetornarOkQuandoResponderSolicitacaoDeAmizadeCorretamente() throws SolicitacaoNaoCadastradaException {
        doNothing().when(this.solicitacaoService).responderSolicitacao(anyBoolean(), anyLong(), any(Usuario.class));

        RequestResponderSolicitacaoAmizadeDto requestResponderSolicitacaoAmizadeDto = new RequestResponderSolicitacaoAmizadeDto(1L, Boolean.TRUE);

        var response = this.amizadeController.responderSolicitacao(requestResponderSolicitacaoAmizadeDto, new Usuario());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Solicitação processada com sucesso"));
    }

    @Test
    void deveRetornarBadRequestQuandoOcorrerExcecaoConhecidaAoResponderSolicitacaoDeAmizade() throws SolicitacaoNaoCadastradaException {
        doThrow(new SolicitacaoNaoCadastradaException("Erro conhecido")).when(this.solicitacaoService).responderSolicitacao(anyBoolean(), anyLong(), any(Usuario.class));

        RequestResponderSolicitacaoAmizadeDto requestResponderSolicitacaoAmizadeDto = new RequestResponderSolicitacaoAmizadeDto(1L, Boolean.TRUE);

        var response = this.amizadeController.responderSolicitacao(requestResponderSolicitacaoAmizadeDto, new Usuario());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro conhecido"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoResponderSolicitacaoDeAmizade() throws SolicitacaoNaoCadastradaException {
        doThrow(new RuntimeException("Erro inesperado")).when(this.solicitacaoService).responderSolicitacao(anyBoolean(), anyLong(), any(Usuario.class));

        RequestResponderSolicitacaoAmizadeDto requestResponderSolicitacaoAmizadeDto = new RequestResponderSolicitacaoAmizadeDto(1L, Boolean.TRUE);

        var response = this.amizadeController.responderSolicitacao(requestResponderSolicitacaoAmizadeDto, new Usuario());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro inesperado"));
    }

    @Test
    void deveRetornarOkQuandoListarUsuariosPorNomeCorretamente() {
        @SuppressWarnings("unchecked")
        Page<ResponseUsuarioDto> pageMock = mock(Page.class); 

        when(this.amizadeService.listarUsuariosPorNome(anyInt(), anyInt(), anyString())).thenReturn(pageMock);

        var response = this.amizadeController.listarUsuariosPorNome(0, 10, "Nome");

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Consulta realizada com sucesso"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadoAoListarUsuarioPorNome() {
        when(this.amizadeService.listarUsuariosPorNome(anyInt(), anyInt(), anyString())).thenThrow(new RuntimeException("Erro inesperado"));

        var response = this.amizadeController.listarUsuariosPorNome(0, 10, "Nome");

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro inesperado"));
    }

    @Test
    void deveRetornarOkQuandoListarUsuarioPeloIdCorretamente() throws UsuarioNaoCadastradoException {
        when(this.amizadeService.listarUsuarioPorId(1L)).thenReturn(mock(ResponseUsuarioDto.class));

        var response = this.amizadeController.listarUsuarioPorId(1L);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Consulta realizada com sucesso"));
    }

    @Test
    void deveRetornarBadRequestQuandoOcorrerExcecaoConhecidaAoListarUsuarioPeloId() throws UsuarioNaoCadastradoException {
        when(this.amizadeService.listarUsuarioPorId(1L)).thenThrow(new UsuarioNaoCadastradoException("Erro conhecido"));

        var response = this.amizadeController.listarUsuarioPorId(1L);

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro conhecido"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoListarUsuarioPeloId() throws UsuarioNaoCadastradoException {
        when(this.amizadeService.listarUsuarioPorId(1L)).thenThrow(new RuntimeException("Erro inesperado"));

        var response = this.amizadeController.listarUsuarioPorId(1L);

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro inesperado"));
    }

    @Test
    void deveRetornarOkQuandoConferirSeUsuarioEhAmigoCorretamente() throws UsuarioNaoCadastradoException {
        ResponseConferirUsuarioAmigoDto responseConferirUsuarioAmigo = new ResponseConferirUsuarioAmigoDto(Boolean.TRUE);

        when(this.amizadeService.conferirSeEhAmigo(anyLong(), any(Usuario.class))).thenReturn(responseConferirUsuarioAmigo);

        var response = this.amizadeController.conferirSeEhAmigo(1L, new Usuario());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Consulta realizada com sucesso"));
    }

    @Test
    void deveRetornarBadRequestQuandoOcorrerExcecaoConhecidaAoConferirSeUsuarioEhAmigo() throws UsuarioNaoCadastradoException {
        when(this.amizadeService.conferirSeEhAmigo(anyLong(), any(Usuario.class))).thenThrow(new UsuarioNaoCadastradoException("Erro conhecido"));

        var response = this.amizadeController.conferirSeEhAmigo(1L, new Usuario());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro conhecido"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoConferirSeUsuarioEhAmigo() throws UsuarioNaoCadastradoException {
        when(this.amizadeService.conferirSeEhAmigo(anyLong(), any(Usuario.class))).thenThrow(new RuntimeException("Erro inesperado"));

        var response = this.amizadeController.conferirSeEhAmigo(1L, new Usuario());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro inesperado"));
    }

    @Test
    void deveRetornarOkQuandoListarAmigosDeUmUsuarioCorretamente() {
        @SuppressWarnings("unchecked")
        Page<ResponseUsuarioDto> pageMock = mock(Page.class); 

        when(this.amizadeService.listarAmigosDeUmUsuario(anyInt(), anyInt(), anyLong())).thenReturn(pageMock);

        var response = this.amizadeController.listarAmigosDeUmUsuario(0, 10, 1L);

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Consulta realizada com sucesso"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoListarAmigosDeUmUsuario() {
        when(this.amizadeService.listarAmigosDeUmUsuario(anyInt(), anyInt(), anyLong())).thenThrow(new RuntimeException("Erro inesperado"));

        var response = this.amizadeController.listarAmigosDeUmUsuario(0, 10, 1L);

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro inesperado"));
    }

    @Test
    void deveRetornarOkQuandoDesamigarCorretamente() throws UsuarioNaoCadastradoException, AmizadeNaoEncontradaException {
        doNothing().when(this.amizadeService).desamigar(anyLong(), any(Usuario.class));

        var response = this.amizadeController.desamigar(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarBadRequestQuandoOcorrerExcecaoConhecidaAoDesamigar() throws UsuarioNaoCadastradoException, AmizadeNaoEncontradaException {
        doThrow(UsuarioNaoCadastradoException.class).when(this.amizadeService).desamigar(anyLong(), any(Usuario.class));

        var response = this.amizadeController.desamigar(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());

        doThrow(AmizadeNaoEncontradaException.class).when(this.amizadeService).desamigar(anyLong(), any(Usuario.class));

        response = this.amizadeController.desamigar(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoDesamigar() throws UsuarioNaoCadastradoException, AmizadeNaoEncontradaException {
        doThrow(RuntimeException.class).when(this.amizadeService).desamigar(anyLong(), any(Usuario.class));

        var response = this.amizadeController.desamigar(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }
}
