package com.univille.mini_rede_social.postagem.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.notificacao.services.NotificacaoService;
import com.univille.mini_rede_social.postagem.dto.input.RequestCriarComentario;
import com.univille.mini_rede_social.postagem.dto.input.RequestCriarPostagemDto;
import com.univille.mini_rede_social.postagem.dto.output.ResponseComentarioDto;
import com.univille.mini_rede_social.postagem.dto.output.ResponsePostagemDto;
import com.univille.mini_rede_social.postagem.exceptions.ComentarioNaoEncontradoException;
import com.univille.mini_rede_social.postagem.exceptions.ImagemETextoNaoInformadosException;
import com.univille.mini_rede_social.postagem.exceptions.ParametrosInsuficientesComentarioException;
import com.univille.mini_rede_social.postagem.exceptions.PostagemNaoEncontradaException;
import com.univille.mini_rede_social.postagem.models.Postagem;
import com.univille.mini_rede_social.postagem.models.PostagemComentario;
import com.univille.mini_rede_social.postagem.models.PostagemComentarioCurtida;
import com.univille.mini_rede_social.postagem.models.PostagemCurtida;
import com.univille.mini_rede_social.postagem.repositories.PostagemComentarioCurtidaRepository;
import com.univille.mini_rede_social.postagem.repositories.PostagemComentarioRepository;
import com.univille.mini_rede_social.postagem.repositories.PostagemCurtidaRepository;
import com.univille.mini_rede_social.postagem.repositories.PostagemRepository;

@ExtendWith(MockitoExtension.class)
class PostagemServiceTest {
    
    @Mock
    PostagemRepository postagemRepository;

    @Mock
    PostagemCurtidaRepository postagemCurtidaRepository;

    @Mock
    PostagemComentarioRepository postagemComentarioRepository;

    @Mock
    PostagemComentarioCurtidaRepository postagemComentarioCurtidaRepository;

    @Mock
    NotificacaoService notificacaoService;

    PostagemService postagemService;

    @BeforeEach
    void onBefore() {
        this.postagemService = new PostagemService(postagemRepository, postagemCurtidaRepository, postagemComentarioRepository, postagemComentarioCurtidaRepository, notificacaoService);
    }

    @Test
    void deveLancarImagemETextoNaoInformadosExceptionAoCriarPostagem() {
        Assertions.assertThrows(ImagemETextoNaoInformadosException.class, () -> {
            this.postagemService.criarPostagem(new RequestCriarPostagemDto(), new Usuario());
        });
    }

    @Test
    void deveCriarPostagemCorretamente() throws ImagemETextoNaoInformadosException {
        var requestCriarPostagem = new RequestCriarPostagemDto();
        requestCriarPostagem.setImagem("foto");
        requestCriarPostagem.setTexto("teste");

        this.postagemService.criarPostagem(requestCriarPostagem, new Usuario());

        verify(this.postagemRepository, times(1)).save(any(Postagem.class));
    }

    @Test
    void deveLancarPostagemNaoEncontradaExceptionAoCurtirOuDescurtirPostagem() {
        Postagem postagem = null;
        var opt = Optional.ofNullable(postagem);

        when(this.postagemRepository.findByCodigo(anyLong())).thenReturn(opt);

        Assertions.assertThrows(PostagemNaoEncontradaException.class, () -> {
            this.postagemService.curtirOuDescurtirPostagem(1L, new Usuario());
        });
    }

    @Test
    void deveRegistrarCurtidaNaPostagem() throws PostagemNaoEncontradaException {
        var postagem = new Postagem();
        postagem.setCurtidas(1);
        var optPostagem = Optional.ofNullable(postagem);

        when(this.postagemRepository.findByCodigo(anyLong())).thenReturn(optPostagem);

        PostagemCurtida postagemCurtida = null;
        var optPostagemCurtida = Optional.ofNullable(postagemCurtida);

        when(this.postagemCurtidaRepository.findByPostagemAndUsuario(any(Postagem.class), any(Usuario.class))).thenReturn(optPostagemCurtida);

        var usuario = new Usuario();
        usuario.setFoto("foto");
        usuario.setNome("nome");

        postagem.setUsuario(usuario);

        this.postagemService.curtirOuDescurtirPostagem(1L, usuario);

        verify(this.postagemCurtidaRepository, times(1)).save(any(PostagemCurtida.class));
        verify(this.postagemRepository, times(1)).save(any(Postagem.class));
        verify(this.notificacaoService, times(1)).registrarNotificacao(any(Usuario.class), anyString(), any(Date.class), anyString());

        Assertions.assertEquals(2, postagem.getCurtidas()); //Aumentou as curtidas
    }

    @Test
    void deveDescurtirPostagem() throws PostagemNaoEncontradaException {
        var postagem = new Postagem();
        postagem.setCurtidas(1);
        var optPostagem = Optional.ofNullable(postagem);

        when(this.postagemRepository.findByCodigo(anyLong())).thenReturn(optPostagem);

        var postagemCurtida = new PostagemCurtida();
        var optPostagemCurtida = Optional.ofNullable(postagemCurtida);

        when(this.postagemCurtidaRepository.findByPostagemAndUsuario(any(Postagem.class), any(Usuario.class))).thenReturn(optPostagemCurtida);

        var usuario = new Usuario();

        this.postagemService.curtirOuDescurtirPostagem(1L, usuario);

        verify(this.postagemCurtidaRepository, times(1)).delete(any(PostagemCurtida.class));
        verify(this.postagemRepository, times(1)).save(any(Postagem.class));

        Assertions.assertEquals(0, postagem.getCurtidas()); //Diminuiu as curtidas
    }

    @Test
    void deveLancarParametrosInsuficientesComentarioExceptionAoCriarComentario() {
        Assertions.assertThrows(ParametrosInsuficientesComentarioException.class, () -> {
            this.postagemService.criarComentario(new RequestCriarComentario(), new Usuario());
        });
    }

    @Test
    void deveLancarPostagemNaoEncontradaExceptionAoCriarComentario() {
        var requestCriarComentario = new RequestCriarComentario();
        requestCriarComentario.setFoto("foto");
        requestCriarComentario.setTexto("texto");
        requestCriarComentario.setCodigoComentarioPai(1L);
        requestCriarComentario.setCodigoPostagem(1L);


        Postagem postagem = null;
        var optPostagem = Optional.ofNullable(postagem);

        when(this.postagemRepository.findByCodigoAndCodigoComentario(anyLong(), anyLong())).thenReturn(optPostagem);

        Assertions.assertThrows(PostagemNaoEncontradaException.class, () -> {
            this.postagemService.criarComentario(requestCriarComentario, new Usuario());
        });

        requestCriarComentario.setCodigoComentarioPai(null);

        when(this.postagemRepository.findByCodigo(anyLong())).thenReturn(optPostagem);

        Assertions.assertThrows(PostagemNaoEncontradaException.class, () -> {
            this.postagemService.criarComentario(requestCriarComentario, new Usuario());
        });
    }

    @Test
    void deveComentarioNaoEncontradoExceptionAoCriarComentario() {
        var requestCriarComentario = new RequestCriarComentario();
        requestCriarComentario.setFoto("foto");
        requestCriarComentario.setTexto("texto");
        requestCriarComentario.setCodigoComentarioPai(1L);
        requestCriarComentario.setCodigoPostagem(1L);


        var postagem = new Postagem();
        var optPostagem = Optional.ofNullable(postagem);

        when(this.postagemRepository.findByCodigoAndCodigoComentario(anyLong(), anyLong())).thenReturn(optPostagem);

        PostagemComentario postagemComentario = null;
        var optPostagemComentario = Optional.ofNullable(postagemComentario);

        when(this.postagemComentarioRepository.findByCodigo(anyLong())).thenReturn(optPostagemComentario);

        Assertions.assertThrows(ComentarioNaoEncontradoException.class, () -> {
            this.postagemService.criarComentario(requestCriarComentario, new Usuario());
        });
    }

    @Test
    void deveCriarComentarioCorretamente() throws PostagemNaoEncontradaException, ParametrosInsuficientesComentarioException, ComentarioNaoEncontradoException {
        var requestCriarComentario = new RequestCriarComentario();
        requestCriarComentario.setFoto("foto");
        requestCriarComentario.setTexto("texto");
        requestCriarComentario.setCodigoComentarioPai(1L);
        requestCriarComentario.setCodigoPostagem(1L);


        var postagem = new Postagem();
        postagem.setComentarios(0);
        var optPostagem = Optional.ofNullable(postagem);

        when(this.postagemRepository.findByCodigoAndCodigoComentario(anyLong(), anyLong())).thenReturn(optPostagem);

        var postagemComentario = new PostagemComentario();
        postagemComentario.setRespostas(0);
        var optPostagemComentario = Optional.ofNullable(postagemComentario);

        when(this.postagemComentarioRepository.findByCodigo(anyLong())).thenReturn(optPostagemComentario);

        var usuario = new Usuario();
        usuario.setFoto("foto");
        usuario.setNome("nome");

        postagem.setUsuario(usuario);

        this.postagemService.criarComentario(requestCriarComentario, usuario);

        verify(this.postagemComentarioRepository, times(2)).save(any(PostagemComentario.class));
        verify(this.postagemRepository, times(1)).save(any(Postagem.class));
        verify(this.notificacaoService, times(1)).registrarNotificacao(any(Usuario.class), anyString(), any(Date.class), anyString());

        Assertions.assertEquals(1, postagem.getComentarios());
        Assertions.assertEquals(1, postagemComentario.getRespostas());
    }

    @Test
    void deveLancarComentarioNaoEncontradoExceptionAoCurtirOuDescurtirComentario() {
        PostagemComentario postagemComentario = null;
        var opt = Optional.ofNullable(postagemComentario);

        when(this.postagemComentarioRepository.findByCodigo(anyLong())).thenReturn(opt);

        Assertions.assertThrows(ComentarioNaoEncontradoException.class, () -> {
            this.postagemService.curtirOuDescurtirComentario(1L, new Usuario());
        });
    }

    @Test
    void deveCurtirComentario() throws ComentarioNaoEncontradoException {
        var postagemComentario = new PostagemComentario();
        postagemComentario.setCurtidas(0);
        var optPostagemComentario = Optional.ofNullable(postagemComentario);

        when(this.postagemComentarioRepository.findByCodigo(anyLong())).thenReturn(optPostagemComentario);

        PostagemComentarioCurtida postagemComentarioCurtida = null;
        var optPostagemComentarioCurtida = Optional.ofNullable(postagemComentarioCurtida);

        when(this.postagemComentarioCurtidaRepository.findByComentarioAndUsuario(any(PostagemComentario.class), any(Usuario.class))).thenReturn(optPostagemComentarioCurtida);

        var usuario = new Usuario();
        usuario.setFoto("foto");
        usuario.setNome("nome");

        postagemComentario.setUsuario(usuario);

        this.postagemService.curtirOuDescurtirComentario(1L, usuario);

        verify(this.postagemComentarioCurtidaRepository, times(1)).save(any(PostagemComentarioCurtida.class));
        verify(this.postagemComentarioRepository, times(1)).save(any(PostagemComentario.class));
        verify(this.notificacaoService, times(1)).registrarNotificacao(any(Usuario.class), anyString(), any(Date.class), anyString());

        Assertions.assertEquals(1, postagemComentario.getCurtidas());
    }

    @Test
    void deveDescurtirComentario() throws ComentarioNaoEncontradoException {
        var postagemComentario = new PostagemComentario();
        postagemComentario.setCurtidas(1);
        var optPostagemComentario = Optional.ofNullable(postagemComentario);

        when(this.postagemComentarioRepository.findByCodigo(anyLong())).thenReturn(optPostagemComentario);

        var postagemComentarioCurtida = new PostagemComentarioCurtida();
        var optPostagemComentarioCurtida = Optional.ofNullable(postagemComentarioCurtida);

        when(this.postagemComentarioCurtidaRepository.findByComentarioAndUsuario(any(PostagemComentario.class), any(Usuario.class))).thenReturn(optPostagemComentarioCurtida);

        var usuario = new Usuario();

        postagemComentario.setUsuario(usuario);

        this.postagemService.curtirOuDescurtirComentario(1L, usuario);

        verify(this.postagemComentarioCurtidaRepository, times(1)).delete(any(PostagemComentarioCurtida.class));
        verify(this.postagemComentarioRepository, times(1)).save(any(PostagemComentario.class));

        Assertions.assertEquals(0, postagemComentario.getCurtidas());
    }

    @Test
    void deveListarTodasPostagensCorretamente() {
        var responsePostagemDto = mock(ResponsePostagemDto.class);

        var lista1 = new ArrayList<ResponsePostagemDto>();

        lista1.add(responsePostagemDto);

        var pageable = PageRequest.of(0, 10);
        var pageMock = new PageImpl<>(lista1, pageable, lista1.size());

        when(this.postagemRepository.listarTodasPostagensDeAmigosOrdenandoPorData(any(PageRequest.class), anyLong())).thenReturn(pageMock);

        var usuario = new Usuario();
        usuario.setCodigo(1L);

        var response = this.postagemService.listarTodasPostagens(0, 10, usuario);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
    }

    @Test
    void deveListarComentariosCorretamente() {
        var responseComentarioDto = mock(ResponseComentarioDto.class);

        var lista1 = new ArrayList<ResponseComentarioDto>();

        lista1.add(responseComentarioDto);

        var pageable = PageRequest.of(0, 10);
        var pageMock = new PageImpl<>(lista1, pageable, lista1.size());

        when(this.postagemComentarioRepository.listarTodosComentariosOrdenandoPorNumeroRespostas(any(PageRequest.class), anyLong())).thenReturn(pageMock);

        var response = this.postagemService.listarComentarios(0, 10, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
    }

    @Test
    void deveListarPostagensDeUmUsuarioCorretamente() {
        var responsePostagemDto = mock(ResponsePostagemDto.class);

        var lista1 = new ArrayList<ResponsePostagemDto>();

        lista1.add(responsePostagemDto);

        var pageable = PageRequest.of(0, 10);
        var pageMock = new PageImpl<>(lista1, pageable, lista1.size());

        when(this.postagemRepository.listarTodasPostagensDeUmUsuarioOrdenandoPorData(any(PageRequest.class), anyLong())).thenReturn(pageMock);

        var response = this.postagemService.listarPostagensDeUmUsuario(0, 10, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
    }

}
