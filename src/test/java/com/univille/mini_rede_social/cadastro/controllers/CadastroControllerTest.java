package com.univille.mini_rede_social.cadastro.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.univille.mini_rede_social.cadastro.services.CadastroService;
import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.utils.ResponseHandler;

@ExtendWith(MockitoExtension.class)
class CadastroControllerTest {
    
    @Mock
    CadastroService cadastroService;

    CadastroController cadastroController;

    @BeforeEach
    void onBefore() {
        this.cadastroController = new CadastroController(new ResponseHandler(), cadastroService);
    }

    @Test
    void deveRetornarCreatedQuandoCadastrarUsuarioCorretamente() throws UsuarioJaCadastradoException {
        doNothing().when(this.cadastroService).cadastrar(any(RequestCadastro.class));

        var response = this.cadastroController.cadastrarUsuario(new RequestCadastro());

        Assertions.assertEquals(201, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Usuário cadastrado com sucesso"));
    }

    @Test
    void deveRetornarBadRequestQuandoLancarUsuarioJaCadastradoExceptionAoCadastrarUsuario() throws UsuarioJaCadastradoException {
        doThrow(UsuarioJaCadastradoException.class).when(this.cadastroService).cadastrar(any(RequestCadastro.class));

        var response = this.cadastroController.cadastrarUsuario(new RequestCadastro());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao realizar cadastro"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExceptionInesperadaAoCadastrarUsuario() throws UsuarioJaCadastradoException {
        doThrow(RuntimeException.class).when(this.cadastroService).cadastrar(any(RequestCadastro.class));

        var response = this.cadastroController.cadastrarUsuario(new RequestCadastro());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao realizar cadastro"));
    }

    @Test
    void deveRetornarOkQuandoConfirmarEmailCorretamente() throws UsuarioNaoCadastradoException, EmailJaConfirmadoException, ConfirmacaoNaoEncontradaException, CodigoConfirmacaoExpiradoException {
        doNothing().when(this.cadastroService).confirmarEmail(any(RequestConfirmarEmail.class));

        var response = this.cadastroController.confirmarEmail(new RequestConfirmarEmail());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Email confirmado com sucesso"));
    }

    @Test
    void deveRetornarBadRequestQuandoLancarExcecaoConhecidaAoConfirmarEmail() throws UsuarioNaoCadastradoException, EmailJaConfirmadoException, ConfirmacaoNaoEncontradaException, CodigoConfirmacaoExpiradoException {
        doThrow(UsuarioNaoCadastradoException.class).when(this.cadastroService).confirmarEmail(any(RequestConfirmarEmail.class));

        var response = this.cadastroController.confirmarEmail(new RequestConfirmarEmail());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao realizar confirmação de email"));

        doThrow(EmailJaConfirmadoException.class).when(this.cadastroService).confirmarEmail(any(RequestConfirmarEmail.class));

        response = this.cadastroController.confirmarEmail(new RequestConfirmarEmail());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao realizar confirmação de email"));

        doThrow(ConfirmacaoNaoEncontradaException.class).when(this.cadastroService).confirmarEmail(any(RequestConfirmarEmail.class));

        response = this.cadastroController.confirmarEmail(new RequestConfirmarEmail());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao realizar confirmação de email"));

        doThrow(CodigoConfirmacaoExpiradoException.class).when(this.cadastroService).confirmarEmail(any(RequestConfirmarEmail.class));

        response = this.cadastroController.confirmarEmail(new RequestConfirmarEmail());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao realizar confirmação de email"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoConfirmarEmail() throws UsuarioNaoCadastradoException, EmailJaConfirmadoException, ConfirmacaoNaoEncontradaException, CodigoConfirmacaoExpiradoException {
        doThrow(RuntimeException.class).when(this.cadastroService).confirmarEmail(any(RequestConfirmarEmail.class));

        var response = this.cadastroController.confirmarEmail(new RequestConfirmarEmail());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao realizar confirmação de email"));
    }

    @Test
    void deveRetornarOkQuandoReenviarEmailConfirmacaoCorretamente() throws UsuarioNaoCadastradoException, EmailJaConfirmadoException {
        doNothing().when(this.cadastroService).reenviarEmailConfirmacao(any(RequestReenviarEmailConfirmacao.class));

        var response = this.cadastroController.reenviarEmailConfirmacao(new RequestReenviarEmailConfirmacao());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Reenviado email com sucesso"));
    }

    @Test
    void deveRetornarBadRequestQuandoOcorrerExcecaoConhecidaAoReenviarEmailConfirmacao() throws UsuarioNaoCadastradoException, EmailJaConfirmadoException {
        doThrow(UsuarioNaoCadastradoException.class).when(this.cadastroService).reenviarEmailConfirmacao(any(RequestReenviarEmailConfirmacao.class));

        var response = this.cadastroController.reenviarEmailConfirmacao(new RequestReenviarEmailConfirmacao());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao reenviar email de confirmação"));

        doThrow(EmailJaConfirmadoException.class).when(this.cadastroService).reenviarEmailConfirmacao(any(RequestReenviarEmailConfirmacao.class));

        response = this.cadastroController.reenviarEmailConfirmacao(new RequestReenviarEmailConfirmacao());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao reenviar email de confirmação"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoReenviarEmailConfirmacao() throws UsuarioNaoCadastradoException, EmailJaConfirmadoException {
        doThrow(RuntimeException.class).when(this.cadastroService).reenviarEmailConfirmacao(any(RequestReenviarEmailConfirmacao.class));

        var response = this.cadastroController.reenviarEmailConfirmacao(new RequestReenviarEmailConfirmacao());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao reenviar email de confirmação"));
    }

    @Test
    void deveRetornarOkQuandoAlterarDadosCadastraisCorretamente() {
        doNothing().when(this.cadastroService).alterarDadosCadastrais(any(RequestAlterarDadosCadastrais.class), any(Usuario.class));

        var response = this.cadastroController.alterarDadosCadastrais(new RequestAlterarDadosCadastrais(), new Usuario());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Alterado dados com sucesso"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoAlterarDadosCadastrais() {
        doThrow(RuntimeException.class).when(this.cadastroService).alterarDadosCadastrais(any(RequestAlterarDadosCadastrais.class), any(Usuario.class));

        var response = this.cadastroController.alterarDadosCadastrais(new RequestAlterarDadosCadastrais(), new Usuario());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao alterar dados"));
    }

    @Test
    void deveRetornarOkQuandoSolicitarTrocaSenhaCorretamente() throws UsuarioNaoCadastradoException {
        doNothing().when(this.cadastroService).solicitarTrocaSenha(any(RequestSolicitarTrocaSenha.class));

        var response = this.cadastroController.solicitarTrocaSenha(new RequestSolicitarTrocaSenha());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Solicitado troca de senha com sucesso"));
    }

    @Test
    void deveRetornarBadRequestQuandoLancarUsuarioNaoCadastradoExceptionAoSolicitarTrocaSenha() throws UsuarioNaoCadastradoException {
        doThrow(UsuarioNaoCadastradoException.class).when(this.cadastroService).solicitarTrocaSenha(any(RequestSolicitarTrocaSenha.class));

        var response = this.cadastroController.solicitarTrocaSenha(new RequestSolicitarTrocaSenha());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao solicitar troca de senha"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoSolicitarTrocaSenha() throws UsuarioNaoCadastradoException {
        doThrow(RuntimeException.class).when(this.cadastroService).solicitarTrocaSenha(any(RequestSolicitarTrocaSenha.class));

        var response = this.cadastroController.solicitarTrocaSenha(new RequestSolicitarTrocaSenha());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao solicitar troca de senha"));
    }

    @Test
    void deveRetornarOkQuandoConfirmarTrocaSenhaCorretamente() throws UsuarioNaoCadastradoException, ConfirmacaoNaoEncontradaException, CodigoConfirmacaoExpiradoException {
        doNothing().when(this.cadastroService).confirmarTrocaSenha(any(RequestConfirmarTrocaSenha.class));

        var response = this.cadastroController.confirmarTrocaSenha(new RequestConfirmarTrocaSenha());

        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Senha alterada com sucesso"));
    }

    @Test
    void deveRetorarBadRequestQuandoOcorrerExcecaoConhecidaAoConfirmarTrocaSenha() throws UsuarioNaoCadastradoException, ConfirmacaoNaoEncontradaException, CodigoConfirmacaoExpiradoException {
        doThrow(UsuarioNaoCadastradoException.class).when(this.cadastroService).confirmarTrocaSenha(any(RequestConfirmarTrocaSenha.class));

        var response = this.cadastroController.confirmarTrocaSenha(new RequestConfirmarTrocaSenha());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao confirmar troca de senha"));

        doThrow(ConfirmacaoNaoEncontradaException.class).when(this.cadastroService).confirmarTrocaSenha(any(RequestConfirmarTrocaSenha.class));

        response = this.cadastroController.confirmarTrocaSenha(new RequestConfirmarTrocaSenha());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao confirmar troca de senha"));

        doThrow(CodigoConfirmacaoExpiradoException.class).when(this.cadastroService).confirmarTrocaSenha(any(RequestConfirmarTrocaSenha.class));

        response = this.cadastroController.confirmarTrocaSenha(new RequestConfirmarTrocaSenha());

        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao confirmar troca de senha"));
    }

    @Test
    void deveRetornarInternalErrorQuandoOcorrerExcecaoInesperadaAoConfirmarTrocaSenha() throws UsuarioNaoCadastradoException, ConfirmacaoNaoEncontradaException, CodigoConfirmacaoExpiradoException {
        doThrow(RuntimeException.class).when(this.cadastroService).confirmarTrocaSenha(any(RequestConfirmarTrocaSenha.class));

        var response = this.cadastroController.confirmarTrocaSenha(new RequestConfirmarTrocaSenha());

        Assertions.assertEquals(500, response.getStatusCode().value());
        Assertions.assertTrue(response.getBody().toString().contains("Erro ao confirmar troca de senha"));
    }

}
