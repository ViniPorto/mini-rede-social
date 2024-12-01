package com.univille.mini_rede_social.notificacao.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.univille.mini_rede_social.notificacao.exceptions.NotificacaoNaoEncontradaException;
import com.univille.mini_rede_social.notificacao.models.Notificacao;
import com.univille.mini_rede_social.notificacao.repositories.NotificacaoRepository;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {
    
    @Mock
    NotificacaoRepository notificacaoRepository;

    NotificacaoService notificacaoService;

    @BeforeEach
    void onBefore() {
        this.notificacaoService = new NotificacaoService(notificacaoRepository);
    }

    @Test
    void deveRetornarPageDeResponseNotificacaoDtoAoListarNovasCorretamente() {
        var notificacao = new Notificacao();
        notificacao.setCodigo(1L);
        notificacao.setDataCriacao(new Date());
        notificacao.setDescricao("");
        notificacao.setFoto("");
        notificacao.setLida(Boolean.TRUE);
        notificacao.setUsuario(new Usuario());
        var lista1 = new ArrayList<Notificacao>();

        lista1.add(notificacao);

        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(lista1, pageable, lista1.size());

        when(this.notificacaoRepository.findAllByUsuarioAndLidaFalseOrderByDataCriacaoDesc(any(PageRequest.class), any(Usuario.class))).thenReturn(page);

        var response = this.notificacaoService.listarNovas(0, 10, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
    }

    @Test
    void deveRetornarPageDeResponseNotificacaoAoListarTodasCorretamente() {
        var notificacao = new Notificacao();
        notificacao.setCodigo(1L);
        notificacao.setDataCriacao(new Date());
        notificacao.setDescricao("");
        notificacao.setFoto("");
        notificacao.setLida(Boolean.TRUE);
        notificacao.setUsuario(new Usuario());
        var lista1 = new ArrayList<Notificacao>();

        lista1.add(notificacao);

        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(lista1, pageable, lista1.size());

        when(this.notificacaoRepository.findAllByUsuarioOrderByDataCriacaoDesc(any(PageRequest.class), any(Usuario.class))).thenReturn(page);

        var response = this.notificacaoService.listarTodas(0, 10, new Usuario());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
    }

    @Test
    void deveLancarNotificacaoNaoEncontradaExceptionAoConfirmarLeitura() {
        Notificacao notificacao = null;
        var opt = Optional.ofNullable(notificacao);

        when(this.notificacaoRepository.findByCodigoAndUsuario(anyLong(), any(Usuario.class))).thenReturn(opt);

        Assertions.assertThrows(NotificacaoNaoEncontradaException.class, () -> {
            this.notificacaoService.confirmarLeitura(1L, new Usuario());
        });
    }

    @Test
    void deveConfirmarLeituraCorretamente() throws NotificacaoNaoEncontradaException {
        var notificacao = new Notificacao();
        var opt = Optional.ofNullable(notificacao);

        when(this.notificacaoRepository.findByCodigoAndUsuario(anyLong(), any(Usuario.class))).thenReturn(opt);

        this.notificacaoService.confirmarLeitura(1L, new Usuario());

        verify(this.notificacaoRepository, times(1)).save(any(Notificacao.class));
    }

    @Test
    void deveRegistrarNotificacaoCorretamente() {
        this.notificacaoService.registrarNotificacao(new Usuario(), "", new Date(), "");

        verify(this.notificacaoRepository, times(1)).save(any(Notificacao.class));
    }

}
