package com.univille.mini_rede_social.cadastro.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.univille.mini_rede_social.cadastro.converters.RequestCadastroDtoParaUsuarioConverter;
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
import com.univille.mini_rede_social.cadastro.models.ConfirmacaoEmail;
import com.univille.mini_rede_social.cadastro.models.EsquecimentoSenha;
import com.univille.mini_rede_social.cadastro.repositories.ConfirmacaoEmailRepository;
import com.univille.mini_rede_social.cadastro.repositories.EsquecimentoSenhaRepository;
import com.univille.mini_rede_social.email.services.EmailService;
import com.univille.mini_rede_social.infra.AppConfigurations;
import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.login.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CadastroServiceTest {
    
    @Mock
    EmailService emailService;

    @Mock
    ConfirmacaoEmailRepository confirmacaoEmailRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    EsquecimentoSenhaRepository esquecimentoSenhaRepository;

    @Mock
    RequestCadastroDtoParaUsuarioConverter requestCadastroDtoParaUsuarioConverter;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AppConfigurations appConfigurations;

    CadastroService cadastroService;

    @BeforeEach
    void onBefore() {
        this.cadastroService = new CadastroService(emailService, confirmacaoEmailRepository, usuarioRepository, esquecimentoSenhaRepository, requestCadastroDtoParaUsuarioConverter, passwordEncoder, appConfigurations);
    }

    @Test
    void deveLancarUsuarioJaCadastradoExceptionQuandoTentarCadastrarUsuarioComEmailRepetido() {
        when(this.usuarioRepository.existsByEmail(anyString())).thenReturn(Boolean.TRUE);

        var requestCadastro = new RequestCadastro();
        requestCadastro.setEmail("email@gmail.com");

        Assertions.assertThrows(UsuarioJaCadastradoException.class, () -> {
            this.cadastroService.cadastrar(requestCadastro);
        });
    }

    @Test
    void deveCadastrarUsuarioCorretamente() throws UsuarioJaCadastradoException {
        var requestCadastro = new RequestCadastro("", "", "", "", new Date(), "", 1.00, 1.00);

        when(this.usuarioRepository.existsByEmail(anyString())).thenReturn(Boolean.FALSE);

        when(this.passwordEncoder.encode(anyString())).thenReturn("");

        var usuario = new Usuario();
        usuario.setNome("nome");
        usuario.setEmail("email");

        when(this.requestCadastroDtoParaUsuarioConverter.converter(any(RequestCadastro.class))).thenReturn(usuario);

        when(this.appConfigurations.getMinutosParaEnviarCodigoConfirmacao()).thenReturn(2);

        this.cadastroService.cadastrar(requestCadastro);

        verify(this.usuarioRepository, times(1)).save(any(Usuario.class));

        verify(this.confirmacaoEmailRepository, times(1)).save(any(ConfirmacaoEmail.class));

        verify(this.emailService, times(1)).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveLancarUsuarioNaoCadastradoExceptionQuandoNaoEncontrarUsuarioPeloEmailAoConfirmarEmail() {
        Usuario usuario = null;
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        Assertions.assertThrows(UsuarioNaoCadastradoException.class, () -> {
            var confirmarEmail = new RequestConfirmarEmail();
            confirmarEmail.setEmail("email");
            this.cadastroService.confirmarEmail(confirmarEmail);
        });
    }

    @Test
    void deveLancarEmailJaConfirmadoExceptionAoConfirmarEmail() {
        var usuario = new Usuario();
        usuario.setEmailConfirmado(Boolean.TRUE);
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        Assertions.assertThrows(EmailJaConfirmadoException.class, () -> {
            var confirmarEmail = new RequestConfirmarEmail();
            confirmarEmail.setEmail("email");
            this.cadastroService.confirmarEmail(confirmarEmail);
        });
    }

    @Test
    void deveLancarConfirmacaoNaoEncontradaExceptionAoConfirmarEmail() {
        var usuario = new Usuario();
        usuario.setEmailConfirmado(Boolean.FALSE);
        var optUsuario = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(optUsuario);

        ConfirmacaoEmail confirmacaoEmail = null;
        var optConfirmacao = Optional.ofNullable(confirmacaoEmail);

        when(this.confirmacaoEmailRepository.findByCodigoConfirmacaoAndUsuario(anyString(), any(Usuario.class))).thenReturn(optConfirmacao);

        Assertions.assertThrows(ConfirmacaoNaoEncontradaException.class, () -> {
            var confirmarEmail = new RequestConfirmarEmail();
            confirmarEmail.setEmail("email");
            confirmarEmail.setCodigoConfirmacao("123");
            this.cadastroService.confirmarEmail(confirmarEmail);
        });
    }

    @Test
    void deveLancarCodigoConfirmacaoExpiradoExceptionAoConfirmarEmail() {
        var usuario = new Usuario();
        usuario.setEmailConfirmado(Boolean.FALSE);
        var optUsuario = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(optUsuario);

        var confirmacaoEmail = new ConfirmacaoEmail();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date umDiaAntes = calendar.getTime();
        confirmacaoEmail.setDataExpiracao(umDiaAntes);
        
        var optConfirmacao = Optional.ofNullable(confirmacaoEmail);

        when(this.confirmacaoEmailRepository.findByCodigoConfirmacaoAndUsuario(anyString(), any(Usuario.class))).thenReturn(optConfirmacao);

        Assertions.assertThrows(CodigoConfirmacaoExpiradoException.class, () -> {
            var confirmarEmail = new RequestConfirmarEmail();
            confirmarEmail.setEmail("email");
            confirmarEmail.setCodigoConfirmacao("123");
            this.cadastroService.confirmarEmail(confirmarEmail);
        });
    }

    @Test
    void deveConfirmarEmailCorretamente() throws UsuarioNaoCadastradoException, EmailJaConfirmadoException, ConfirmacaoNaoEncontradaException, CodigoConfirmacaoExpiradoException {
        var usuario = new Usuario();
        usuario.setEmailConfirmado(Boolean.FALSE);
        var optUsuario = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(optUsuario);

        var confirmacaoEmail = new ConfirmacaoEmail();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date umDiaAntes = calendar.getTime();
        confirmacaoEmail.setDataExpiracao(umDiaAntes);
        
        var optConfirmacao = Optional.ofNullable(confirmacaoEmail);

        when(this.confirmacaoEmailRepository.findByCodigoConfirmacaoAndUsuario(anyString(), any(Usuario.class))).thenReturn(optConfirmacao);

        var confirmarEmail = new RequestConfirmarEmail();
        confirmarEmail.setEmail("email");
        confirmarEmail.setCodigoConfirmacao("123");
        this.cadastroService.confirmarEmail(confirmarEmail);
      
        verify(this.usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarUsuarioNaoCadastradoExceptionAoReenviarEmailConfirmacao() {
        Usuario usuario = null;
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        Assertions.assertThrows(UsuarioNaoCadastradoException.class, () -> {
            var confirmacaoEmail = new RequestReenviarEmailConfirmacao();
            confirmacaoEmail.setEmail("email");
            this.cadastroService.reenviarEmailConfirmacao(confirmacaoEmail);
        });
    }

    @Test
    void deveLancarEmailJaConfirmadoExceptionAoReenviarEmailConfirmacao() {
        var usuario = new Usuario();
        usuario.setEmailConfirmado(Boolean.TRUE);
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        Assertions.assertThrows(EmailJaConfirmadoException.class, () -> {
            var confirmacaoEmail = new RequestReenviarEmailConfirmacao();
            confirmacaoEmail.setEmail("email");
            this.cadastroService.reenviarEmailConfirmacao(confirmacaoEmail);
        });
    }

    @Test
    void deveReenviarEmailConfirmacaoCorretamente() throws UsuarioNaoCadastradoException, EmailJaConfirmadoException {
        var usuario = new Usuario();
        usuario.setEmailConfirmado(Boolean.FALSE);
        usuario.setEmail("email");
        usuario.setNome("noma");
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        var confirmacaoEmail = new RequestReenviarEmailConfirmacao();
        confirmacaoEmail.setEmail("email");
        this.cadastroService.reenviarEmailConfirmacao(confirmacaoEmail);

        verify(this.confirmacaoEmailRepository, times(1)).save(any(ConfirmacaoEmail.class));

        verify(this.emailService, times(1)).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveAlterarDadosCadastraisCorretamente() {
        var requestAlterarDadosCadastrais = new RequestAlterarDadosCadastrais("", "", any(Date.class), "", "", 1.00, 1.00);

        this.cadastroService.alterarDadosCadastrais(requestAlterarDadosCadastrais, new Usuario());

        verify(this.usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveLancarUsuarioNaoCadastradoExceptionAoSolicitarTrocaSenha() {
        Usuario usuario = null;
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        Assertions.assertThrows(UsuarioNaoCadastradoException.class, () -> {
            var solicitarTrocaSenha = new RequestSolicitarTrocaSenha();
            solicitarTrocaSenha.setEmail("email");
            this.cadastroService.solicitarTrocaSenha(solicitarTrocaSenha);
        });
    }

    @Test
    void deveSolicitarTrocaSenhaCorretamente() throws UsuarioNaoCadastradoException {
        var usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setNome("noma");
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        when(this.appConfigurations.getMinutosParaEnviarCodigoConfirmacao()).thenReturn(2);

        when(this.passwordEncoder.encode(anyString())).thenReturn("");

        var solicitarTrocaSenha = new RequestSolicitarTrocaSenha();
        solicitarTrocaSenha.setEmail("email");
        solicitarTrocaSenha.setNovaSenha("");
        this.cadastroService.solicitarTrocaSenha(solicitarTrocaSenha);

        verify(this.esquecimentoSenhaRepository, times(1)).deleteByUsuario(any(Usuario.class));

        verify(this.esquecimentoSenhaRepository, times(1)).save(any(EsquecimentoSenha.class));

        verify(this.emailService, times(1)).enviarEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deveLancarUsuarioNaoCadastradoExceptionAoConfirmarTrocaSenha() {
        Usuario usuario = null;
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        Assertions.assertThrows(UsuarioNaoCadastradoException.class, () -> {
            var requestConfirmarTrocaSenha = new RequestConfirmarTrocaSenha();
            requestConfirmarTrocaSenha.setEmail("email");
            this.cadastroService.confirmarTrocaSenha(requestConfirmarTrocaSenha);
        });
    }

    @Test
    void deveLancarConfirmacaoNaoEncontradaExceptionAoConfirmarTrocaSenha() {
        var usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setNome("noma");
        var optUsuario = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(optUsuario);

        EsquecimentoSenha esquecimentoSenha = null;
        var optEsquecimentoSenha = Optional.ofNullable(esquecimentoSenha);

        when(this.esquecimentoSenhaRepository.findByCodigoConfirmacaoAndUsuario(anyString(), any(Usuario.class))).thenReturn(optEsquecimentoSenha);

        Assertions.assertThrows(ConfirmacaoNaoEncontradaException.class, () -> {
            var requestConfirmarTrocaSenha = new RequestConfirmarTrocaSenha();
            requestConfirmarTrocaSenha.setEmail("email");
            requestConfirmarTrocaSenha.setCodigoConfirmacao("codigo");
            this.cadastroService.confirmarTrocaSenha(requestConfirmarTrocaSenha);
        });
    }

    @Test
    void deveLancarCodigoConfirmacaoExpiradoExceptionAoConfirmarTrocaSenha() {
        var usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setNome("noma");
        var optUsuario = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(optUsuario);

        var esquecimentoSenha = new EsquecimentoSenha();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date umDiaAntes = calendar.getTime();
        esquecimentoSenha.setDataExpiracao(umDiaAntes);

        var optEsquecimentoSenha = Optional.ofNullable(esquecimentoSenha);

        when(this.esquecimentoSenhaRepository.findByCodigoConfirmacaoAndUsuario(anyString(), any(Usuario.class))).thenReturn(optEsquecimentoSenha);

        Assertions.assertThrows(CodigoConfirmacaoExpiradoException.class, () -> {
            var requestConfirmarTrocaSenha = new RequestConfirmarTrocaSenha();
            requestConfirmarTrocaSenha.setEmail("email");
            requestConfirmarTrocaSenha.setCodigoConfirmacao("codigo");
            this.cadastroService.confirmarTrocaSenha(requestConfirmarTrocaSenha);
        });
    }

    @Test
    void deveConfirmarTrocaSenhaCorretamente() throws UsuarioNaoCadastradoException, ConfirmacaoNaoEncontradaException, CodigoConfirmacaoExpiradoException {
        var usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setNome("noma");
        var optUsuario = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(optUsuario);

        var esquecimentoSenha = new EsquecimentoSenha();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date umDiaAntes = calendar.getTime();
        esquecimentoSenha.setDataExpiracao(umDiaAntes);

        var optEsquecimentoSenha = Optional.ofNullable(esquecimentoSenha);

        when(this.esquecimentoSenhaRepository.findByCodigoConfirmacaoAndUsuario(anyString(), any(Usuario.class))).thenReturn(optEsquecimentoSenha);

        var requestConfirmarTrocaSenha = new RequestConfirmarTrocaSenha();
        requestConfirmarTrocaSenha.setEmail("email");
        requestConfirmarTrocaSenha.setCodigoConfirmacao("codigo");
        this.cadastroService.confirmarTrocaSenha(requestConfirmarTrocaSenha);

        verify(this.usuarioRepository, times(1)).save(any(Usuario.class));
    }

}
