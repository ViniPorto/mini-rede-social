package com.univille.mini_rede_social.amizades.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.data.domain.Pageable;

import com.univille.mini_rede_social.amizades.exceptions.AmizadeNaoEncontradaException;
import com.univille.mini_rede_social.amizades.models.Amizade;
import com.univille.mini_rede_social.amizades.repositories.AmizadeRepository;
import com.univille.mini_rede_social.cadastro.exceptions.UsuarioNaoCadastradoException;
import com.univille.mini_rede_social.infra.AppConfigurations;
import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.login.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AmizadeServiceTest {
    
    @Mock
    AppConfigurations appConfigurations;

    @Mock
    AmizadeRepository amizadeRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    AmizadeService amizadeService;

    @BeforeEach
    void onBefore() {
        this.amizadeService = new AmizadeService(appConfigurations, amizadeRepository, usuarioRepository);
    }

    @Test
    void deveRetornarPageDeResponseUsuarioDtoAoListarTodasAmizadesCorretamente() {
        var usuario = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var usuarios = new ArrayList<Usuario>();

        usuarios.add(usuario);

        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(usuarios, pageable, usuarios.size());

        when(this.amizadeRepository.listarTodasAmizades(any(Pageable.class), anyLong())).thenReturn(page);

        var response = this.amizadeService.listarTodas(0, 10, usuario);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(usuario.getCodigo(), response.getContent().get(0).getCodigo());
    }

    @Test
    void deveRetornarAmizadesSugeridasSemRepetirUsuarios() {
        var usuario1 = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var usuario2 = new Usuario(2L, "teste2@gmail.com", "senha", true, "nome2", "foto", "resumo", new Date(), null, null);

        var lista1 = new ArrayList<Usuario>();
        var lista2 = new ArrayList<Usuario>();

        lista1.add(usuario1);
        lista1.add(usuario2);

        lista2.add(usuario1);
        lista2.add(usuario2);

        when(this.appConfigurations.getRaioEmKmSugestaoAmizade()).thenReturn(1);
        when(this.appConfigurations.getLimiteSugestaoAmigosAmigos()).thenReturn(5);
        when(this.appConfigurations.getLimiteSugestaoPessoasProximas()).thenReturn(5);

        when(this.amizadeRepository.listarAmigosDeAmigosAleatorios(anyLong(), any(Pageable.class))).thenReturn(lista1);
        when(this.amizadeRepository.listarPessoasProximasAleatorias(anyInt(), any(), any(), any(Pageable.class), anyLong())).thenReturn(lista2);

        var response = this.amizadeService.listarSugeridos(usuario1);

        Assertions.assertEquals(2, response.size());
        Assertions.assertNotEquals(response.get(0), response.get(1));
    }

    @Test
    void deveRetornarPageDeResponseUsuarioDtoQuandoListarUsuariosPorNomeCorretamente() {
        var usuario1 = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var usuario2 = new Usuario(2L, "teste2@gmail.com", "senha", true, "nome2", "foto", "resumo", new Date(), null, null);

        var lista1 = new ArrayList<Usuario>();

        lista1.add(usuario1);
        lista1.add(usuario2);

        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(lista1, pageable, lista1.size());

        when(this.usuarioRepository.findAllByNomeLike(any(PageRequest.class), anyString())).thenReturn(page);

        var response = this.amizadeService.listarUsuariosPorNome(0, 10, "nome");

        Assertions.assertEquals(2, response.getContent().size());
    }

    @Test
    void deveRetornarResponseUsuarioDtoQuandoEncontrarUsuarioAoListarUsuarioPorId() throws UsuarioNaoCadastradoException {
        var usuario = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        var response = this.amizadeService.listarUsuarioPorId(1L);

        Assertions.assertNotNull(response);
    }

    @Test
    void deveLancarUsuarioNaoCadastradoExceptionQuandoNaoEncontrarUsuarioAoListarUsuarioPorId() {
        Usuario usuario = null;
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        Assertions.assertThrows(UsuarioNaoCadastradoException.class, () -> {
            this.amizadeService.listarUsuarioPorId(1L);
        });
    }

    @Test
    void deveLancarUsuarioNaoCadastradoExceptionQuandoNaoEncontrarUsuarioAoConferirSeEhAmigo() {
        Usuario usuario = null;
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        Assertions.assertThrows(UsuarioNaoCadastradoException.class, () -> {
            this.amizadeService.conferirSeEhAmigo(1L, usuario);
        });
    }

    @Test
    void deveRetornarResponseConferirUsuarioAmigoDtoFalseQuandoNaoEncontrarAmizadeAoConferirSeEhAmigo() throws UsuarioNaoCadastradoException {
        var usuario = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        when(this.amizadeRepository.existsByUsuarioPrincipalAndUsuarioAmigo(any(Usuario.class), any(Usuario.class))).thenReturn(Boolean.FALSE);

        var response = this.amizadeService.conferirSeEhAmigo(1L, usuario);
        
        Assertions.assertFalse(response.getEhAmigo());
    }

    @Test
    void deveRetornarResponseConferirUsuarioAmigoDtoTrueQuandoEncontrarAmizadeAoConferirSeEhAmigo() throws UsuarioNaoCadastradoException {
        var usuario = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        when(this.amizadeRepository.existsByUsuarioPrincipalAndUsuarioAmigo(any(Usuario.class), any(Usuario.class))).thenReturn(Boolean.TRUE);

        var response = this.amizadeService.conferirSeEhAmigo(1L, usuario);
        
        Assertions.assertTrue(response.getEhAmigo());
    }

    @Test
    void deveRetornarPageDeResponseUsuarioDtoQuandoListarAmigosDeUmUsuarioCorretamente() {
        var usuario1 = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var usuario2 = new Usuario(2L, "teste2@gmail.com", "senha", true, "nome2", "foto", "resumo", new Date(), null, null);

        var lista1 = new ArrayList<Usuario>();

        lista1.add(usuario1);
        lista1.add(usuario2);

        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(lista1, pageable, lista1.size());

        when(this.amizadeRepository.listarTodasAmizades(any(PageRequest.class), anyLong())).thenReturn(page);

        var response = this.amizadeService.listarAmigosDeUmUsuario(0, 10, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.getContent().size());
    }

    @Test
    void deveLancarUsuarioNaoCadastradoExceptionQuandoNaoEncontrarUsuarioAoDesamigar() {
        Usuario usuario = null;
        var usuarioOpt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(usuarioOpt);

        Assertions.assertThrows(UsuarioNaoCadastradoException.class, () -> {
            this.amizadeService.desamigar(1L, new Usuario());
        });
    }

    @Test
    void deveLancarAmizadeNaoEncontradaExceptionQuandoNaoEncontrarAmizadeAoDesamigar() {
        var usuario = new Usuario();
        var usuarioOpt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(usuarioOpt);

        Amizade amizade = null;
        var amizadeOpt = Optional.ofNullable(amizade);

        when(this.amizadeRepository.findByUsuarioPrincipalAndUsuarioAmigo(any(Usuario.class), any(Usuario.class))).thenReturn(amizadeOpt).thenReturn(amizadeOpt);

        Assertions.assertThrows(AmizadeNaoEncontradaException.class, () -> {
            this.amizadeService.desamigar(1L, new Usuario());
        });
    }

    @Test
    void deveDesamigarCorretamente() throws UsuarioNaoCadastradoException, AmizadeNaoEncontradaException {
        var usuario = new Usuario();
        var usuarioOpt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(usuarioOpt);

        var amizade = new Amizade();
        var amizadeOpt = Optional.ofNullable(amizade);

        when(this.amizadeRepository.findByUsuarioPrincipalAndUsuarioAmigo(any(Usuario.class), any(Usuario.class))).thenReturn(amizadeOpt).thenReturn(amizadeOpt);

        this.amizadeService.desamigar(1L, new Usuario());

        verify(this.amizadeRepository, times(2)).delete(any(Amizade.class));
    }

}
