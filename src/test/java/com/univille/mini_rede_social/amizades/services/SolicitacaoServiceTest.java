package com.univille.mini_rede_social.amizades.services;

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

import com.univille.mini_rede_social.amizades.exceptions.AmizadeJaExistenteException;
import com.univille.mini_rede_social.amizades.exceptions.SolicitacaoJaEnviadaExcetion;
import com.univille.mini_rede_social.amizades.exceptions.SolicitacaoNaoCadastradaException;
import com.univille.mini_rede_social.amizades.exceptions.UsuarioRepetidoException;
import com.univille.mini_rede_social.amizades.models.Amizade;
import com.univille.mini_rede_social.amizades.models.Solicitacao;
import com.univille.mini_rede_social.amizades.repositories.AmizadeRepository;
import com.univille.mini_rede_social.amizades.repositories.SolicitacaoRepository;
import com.univille.mini_rede_social.cadastro.exceptions.UsuarioNaoCadastradoException;
import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.login.repositories.UsuarioRepository;
import com.univille.mini_rede_social.notificacao.services.NotificacaoService;

@ExtendWith(MockitoExtension.class)
class SolicitacaoServiceTest {
    
    @Mock
    SolicitacaoRepository solicitacaoRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    AmizadeRepository amizadeRepository;

    @Mock
    NotificacaoService notificacaoService;

    SolicitacaoService solicitacaoService;

    @BeforeEach
    void onBefore() {
        this.solicitacaoService = new SolicitacaoService(solicitacaoRepository, usuarioRepository, amizadeRepository, notificacaoService);
    }

    @Test
    void deveLancarUsuarioNaoCadastradoExceptionQuandoCriarSolicitacaoParaUsuarioInexistente() {
        Usuario usuario = null;
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        Assertions.assertThrows(UsuarioNaoCadastradoException.class, () -> {
            this.solicitacaoService.criarSolicitacao(1L, usuario);
        });
    }

    @Test
    void deveLancarUsuarioRepetidoExceptionQuandoOUsuarioAAdicionarEhOMesmoQueEstaSolicitando() {
        var usuario1 = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var usuario2 = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var opt = Optional.ofNullable(usuario1);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        Assertions.assertThrows(UsuarioRepetidoException.class, () -> {
            this.solicitacaoService.criarSolicitacao(1L, usuario2);
        });
    }

    @Test
    void deveLancarSolicitacaoJaEnviadaExceptionQuandoJaExistirSolicitacaoParaOUsuario() {
        var usuario1 = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var usuario2 = new Usuario(2L, "teste2@gmail.com", "senha", true, "nome2", "foto", "resumo", new Date(), null, null);
        var opt = Optional.ofNullable(usuario1);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        when(this.solicitacaoRepository.existsByUsuarioDestinatarioAndUsuarioRemetente(any(Usuario.class), any(Usuario.class))).thenReturn(Boolean.TRUE);

        Assertions.assertThrows(SolicitacaoJaEnviadaExcetion.class, () -> {
            this.solicitacaoService.criarSolicitacao(1L, usuario2);
        });
    }

    @Test
    void deveLancarAmizadeJaExistenteExceptionQuandoEnviarSolicitacaoParaUmUsuarioQueJaEhAmigo() {
        var usuario1 = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var usuario2 = new Usuario(2L, "teste2@gmail.com", "senha", true, "nome2", "foto", "resumo", new Date(), null, null);
        var opt = Optional.ofNullable(usuario1);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        when(this.solicitacaoRepository.existsByUsuarioDestinatarioAndUsuarioRemetente(any(Usuario.class), any(Usuario.class))).thenReturn(Boolean.FALSE);

        when(this.amizadeRepository.existsByUsuarioPrincipalAndUsuarioAmigo(any(Usuario.class), any(Usuario.class))).thenReturn(Boolean.TRUE);

        Assertions.assertThrows(AmizadeJaExistenteException.class, () -> {
            this.solicitacaoService.criarSolicitacao(1L, usuario2);
        });
    }

    @Test
    void deveSalvarSolicitacaoEEnviarNotificacaoAoCriarSolicitacaoCorretamente() throws UsuarioNaoCadastradoException, UsuarioRepetidoException, SolicitacaoJaEnviadaExcetion, AmizadeJaExistenteException {
        var usuario1 = new Usuario(1L, "teste@gmail.com", "senha", true, "nome", "foto", "resumo", new Date(), null, null);
        var usuario2 = new Usuario(2L, "teste2@gmail.com", "senha", true, "nome2", "foto", "resumo", new Date(), null, null);
        var opt = Optional.ofNullable(usuario1);

        when(this.usuarioRepository.findById(anyLong())).thenReturn(opt);

        when(this.solicitacaoRepository.existsByUsuarioDestinatarioAndUsuarioRemetente(any(Usuario.class), any(Usuario.class))).thenReturn(Boolean.FALSE);

        when(this.amizadeRepository.existsByUsuarioPrincipalAndUsuarioAmigo(any(Usuario.class), any(Usuario.class))).thenReturn(Boolean.FALSE);

        this.solicitacaoService.criarSolicitacao(1L, usuario2);

        verify(this.solicitacaoRepository, times(1)).save(any(Solicitacao.class));
        verify(this.notificacaoService, times(1)).registrarNotificacao(any(Usuario.class), anyString(), any(Date.class), anyString());
    }

    @Test
    void deveRetornarPageDeResponseSolicitacaoDtoQuandoListarTodasAsSolicitacoesCorretamente() {
        var solicitacao1 = new Solicitacao(1L, mock(Usuario.class), mock(Usuario.class), new Date());
        var solicitacao2 = new Solicitacao(2L, mock(Usuario.class), mock(Usuario.class), new Date());

        var lista1 = new ArrayList<Solicitacao>();

        lista1.add(solicitacao1);
        lista1.add(solicitacao2);

        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(lista1, pageable, lista1.size());

        when(this.solicitacaoRepository.findAllByUsuarioDestinatario(any(PageRequest.class), any(Usuario.class))).thenReturn(page);

        var response = this.solicitacaoService.listarTodas(0, 10, mock(Usuario.class));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.getContent().size());
    }

    @Test
    void deveLancarSolicitacaoNaoCadastradaExceptionAoTentarResponseSolicitacaoNaoExistente() {
        Solicitacao solicitacao = null;
        var opt = Optional.ofNullable(solicitacao);

        when(this.solicitacaoRepository.findByCodigoAndUsuarioDestinatario(anyLong(), any(Usuario.class))).thenReturn(opt);

        Assertions.assertThrows(SolicitacaoNaoCadastradaException.class, () -> {
            this.solicitacaoService.responderSolicitacao(Boolean.TRUE, 1L, mock(Usuario.class));
        });
    }

    @Test
    void deveApenasApagarSolicitacaoQuandoRejeitarAmizade() throws SolicitacaoNaoCadastradaException {
        var solicitacao = new Solicitacao(1L, mock(Usuario.class), mock(Usuario.class), new Date());
        var opt = Optional.ofNullable(solicitacao);

        when(this.solicitacaoRepository.findByCodigoAndUsuarioDestinatario(anyLong(), any(Usuario.class))).thenReturn(opt);

        this.solicitacaoService.responderSolicitacao(Boolean.FALSE, 1L, mock(Usuario.class));

        verify(this.amizadeRepository, times(0)).save(any(Amizade.class));
        verify(this.notificacaoService, times(0)).registrarNotificacao(any(Usuario.class), anyString(), any(Date.class), anyString());

        verify(this.solicitacaoRepository, times(1)).delete(any(Solicitacao.class));
    }
    
    @Test
    void deveCriarAmizadeERegistrarNotificacaoEApagarSolicitacaoAoAceitarSolicitacao() throws SolicitacaoNaoCadastradaException {
        var solicitacao = new Solicitacao(1L, mock(Usuario.class), mock(Usuario.class), new Date());
        var opt = Optional.ofNullable(solicitacao);

        when(this.solicitacaoRepository.findByCodigoAndUsuarioDestinatario(anyLong(), any(Usuario.class))).thenReturn(opt);

        this.solicitacaoService.responderSolicitacao(Boolean.TRUE, 1L, mock(Usuario.class));

        verify(this.amizadeRepository, times(2)).save(any(Amizade.class));
        verify(this.notificacaoService, times(1)).registrarNotificacao(any(Usuario.class), any(), any(Date.class), anyString());

        verify(this.solicitacaoRepository, times(1)).delete(any(Solicitacao.class));
    }
    
}
