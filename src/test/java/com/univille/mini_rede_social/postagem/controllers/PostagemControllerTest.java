package com.univille.mini_rede_social.postagem.controllers;

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
import com.univille.mini_rede_social.postagem.dto.input.RequestCriarComentario;
import com.univille.mini_rede_social.postagem.dto.input.RequestCriarPostagemDto;
import com.univille.mini_rede_social.postagem.dto.output.ResponseComentarioDto;
import com.univille.mini_rede_social.postagem.dto.output.ResponsePostagemDto;
import com.univille.mini_rede_social.postagem.exceptions.ComentarioNaoEncontradoException;
import com.univille.mini_rede_social.postagem.exceptions.ImagemETextoNaoInformadosException;
import com.univille.mini_rede_social.postagem.exceptions.ParametrosInsuficientesComentarioException;
import com.univille.mini_rede_social.postagem.exceptions.PostagemNaoEncontradaException;
import com.univille.mini_rede_social.postagem.services.PostagemService;
import com.univille.mini_rede_social.utils.ResponseHandler;

@ExtendWith(MockitoExtension.class)
class PostagemControllerTest {
 
    @Mock
    PostagemService postagemService;

    PostagemController postagemController;

    @BeforeEach
    void onBefore() {
        this.postagemController = new PostagemController(new ResponseHandler(), postagemService);
    }

    @Test
    void deveRetornarOkQuandoCriarPostagemCorretamente() throws ImagemETextoNaoInformadosException {
        doNothing().when(this.postagemService).criarPostagem(any(RequestCriarPostagemDto.class), any(Usuario.class));

        var response = this.postagemController.criarPostagem(new RequestCriarPostagemDto(), new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void deveRetornarBadRequestQuandoLancarImagemETextoNaoInformadosExceptionAoCriarPostagem() throws ImagemETextoNaoInformadosException {
        doThrow(ImagemETextoNaoInformadosException.class).when(this.postagemService).criarPostagem(any(RequestCriarPostagemDto.class), any(Usuario.class));

        var response = this.postagemController.criarPostagem(new RequestCriarPostagemDto(), new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoCriarPostagem() throws ImagemETextoNaoInformadosException {
        doThrow(RuntimeException.class).when(this.postagemService).criarPostagem(any(RequestCriarPostagemDto.class), any(Usuario.class));

        var response = this.postagemController.criarPostagem(new RequestCriarPostagemDto(), new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deveRetornarOkQuandoCurtirOuDescurtirPostagemCorretamente() throws PostagemNaoEncontradaException {
        doNothing().when(this.postagemService).curtirOuDescurtirPostagem(anyLong(), any(Usuario.class));

        var response = this.postagemController.curtirOuDescurtirPostagem(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarBadRequestQuandoOcorrerPostagemNaoEncontradaExceptionAoCurtirOuDescurtirPostagem() throws PostagemNaoEncontradaException {
        doThrow(PostagemNaoEncontradaException.class).when(this.postagemService).curtirOuDescurtirPostagem(anyLong(), any(Usuario.class));

        var response = this.postagemController.curtirOuDescurtirPostagem(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoCurtirOuDescurtirPostagem() throws PostagemNaoEncontradaException {
        doThrow(RuntimeException.class).when(this.postagemService).curtirOuDescurtirPostagem(anyLong(), any(Usuario.class));

        var response = this.postagemController.curtirOuDescurtirPostagem(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deveRetornarOkQuandoComentarPostagemCorretamente() throws PostagemNaoEncontradaException, ParametrosInsuficientesComentarioException, ComentarioNaoEncontradoException {
        doNothing().when(this.postagemService).criarComentario(any(RequestCriarComentario.class), any(Usuario.class));

        var response = this.postagemController.comentarPostagem(new RequestCriarComentario(), new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void deveRetornarBadRequestQuandoLancarExcecaoConhecidaAoComentarPostagem() throws PostagemNaoEncontradaException, ParametrosInsuficientesComentarioException, ComentarioNaoEncontradoException {
        doThrow(PostagemNaoEncontradaException.class).when(this.postagemService).criarComentario(any(RequestCriarComentario.class), any(Usuario.class));

        var response = this.postagemController.comentarPostagem(new RequestCriarComentario(), new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());

        doThrow(ParametrosInsuficientesComentarioException.class).when(this.postagemService).criarComentario(any(RequestCriarComentario.class), any(Usuario.class));

        response = this.postagemController.comentarPostagem(new RequestCriarComentario(), new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());

        doThrow(ComentarioNaoEncontradoException.class).when(this.postagemService).criarComentario(any(RequestCriarComentario.class), any(Usuario.class));

        response = this.postagemController.comentarPostagem(new RequestCriarComentario(), new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoComentarPostagem() throws PostagemNaoEncontradaException, ParametrosInsuficientesComentarioException, ComentarioNaoEncontradoException {
        doThrow(RuntimeException.class).when(this.postagemService).criarComentario(any(RequestCriarComentario.class), any(Usuario.class));

        var response = this.postagemController.comentarPostagem(new RequestCriarComentario(), new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deveRetornarOkQuandoCurtirOuDescurtirComentarioCorretamente() throws ComentarioNaoEncontradoException {
        doNothing().when(this.postagemService).curtirOuDescurtirComentario(anyLong(), any(Usuario.class));

        var response = this.postagemController.curtirOuDescurtirComentario(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarBadRequestQuandoLancarComentarioNaoEncontradoExceptionAoCurtirOuDescurtirComentario() throws ComentarioNaoEncontradoException {
        doThrow(ComentarioNaoEncontradoException.class).when(this.postagemService).curtirOuDescurtirComentario(anyLong(), any(Usuario.class));

        var response = this.postagemController.curtirOuDescurtirComentario(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoCurtirOuDescurtirComentario() throws ComentarioNaoEncontradoException {
        doThrow(RuntimeException.class).when(this.postagemService).curtirOuDescurtirComentario(anyLong(), any(Usuario.class));

        var response = this.postagemController.curtirOuDescurtirComentario(1L, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deveRetornarOkQuandoListarTodasAsPostagensCorretamente() {
        @SuppressWarnings("unchecked")
        Page<ResponsePostagemDto> pageMock = mock(Page.class);

        when(this.postagemService.listarTodasPostagens(anyInt(), anyInt(), any(Usuario.class))).thenReturn(pageMock);

        var response = this.postagemController.listarTodasPostagens(0, 10, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternarErrorQuandoLancarExcecaoInesperadaAoListarTodasAsPostagens() {
        when(this.postagemService.listarTodasPostagens(anyInt(), anyInt(), any(Usuario.class))).thenThrow(RuntimeException.class);

        var response = this.postagemController.listarTodasPostagens(0, 10, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deveRetornarOkQuandoListarComentariosCorretamente() throws PostagemNaoEncontradaException {
        @SuppressWarnings("unchecked")
        Page<ResponseComentarioDto> pageMock = mock(Page.class);

        when(this.postagemService.listarComentarios(anyInt(), anyInt(), anyLong())).thenReturn(pageMock);

        var response = this.postagemController.listarComentarios(0, 10, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoListarComentarios() throws PostagemNaoEncontradaException {
        when(this.postagemService.listarComentarios(anyInt(), anyInt(), anyLong())).thenThrow(RuntimeException.class);

        var response = this.postagemController.listarComentarios(0, 10, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void deveRetornarOkQuandoListarPostagensDeUmUsuarioCorretamente() {
        @SuppressWarnings("unchecked")
        Page<ResponsePostagemDto> pageMock = mock(Page.class);

        when(this.postagemService.listarPostagensDeUmUsuario(anyInt(), anyInt(), anyLong())).thenReturn(pageMock);

        var response = this.postagemController.listarPostagensDeUmUsuario(0, 10, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoListarPostagensDeUmUsuario() {
        when(this.postagemService.listarPostagensDeUmUsuario(anyInt(), anyInt(), anyLong())).thenThrow(RuntimeException.class);

        var response = this.postagemController.listarPostagensDeUmUsuario(0, 10, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(500, response.getStatusCode().value());
    }
    
}
