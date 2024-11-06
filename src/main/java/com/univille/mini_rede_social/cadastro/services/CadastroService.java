package com.univille.mini_rede_social.cadastro.services;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.univille.mini_rede_social.cadastro.converters.RequestCadastroDtoParaUsuarioConverter;
import com.univille.mini_rede_social.cadastro.dto.input.RequestAlterarDadosCadastrais;
import com.univille.mini_rede_social.cadastro.dto.input.RequestCadastro;
import com.univille.mini_rede_social.cadastro.dto.input.RequestConfirmarEmail;
import com.univille.mini_rede_social.cadastro.dto.input.RequestReenviarEmailConfirmacao;
import com.univille.mini_rede_social.cadastro.exceptions.CodigoConfirmacaoExpiradoException;
import com.univille.mini_rede_social.cadastro.exceptions.ConfirmacaoNaoEncontradaException;
import com.univille.mini_rede_social.cadastro.exceptions.EmailJaConfirmadoException;
import com.univille.mini_rede_social.cadastro.exceptions.UsuarioJaCadastradoException;
import com.univille.mini_rede_social.cadastro.exceptions.UsuarioNaoCadastradoException;
import com.univille.mini_rede_social.cadastro.models.ConfirmacaoEmail;
import com.univille.mini_rede_social.cadastro.repositories.ConfirmacaoEmailRepository;
import com.univille.mini_rede_social.email.services.EmailService;
import com.univille.mini_rede_social.infra.AppConfigurations;
import com.univille.mini_rede_social.login.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CadastroService {

    private final EmailService emailService;
    
    private final ConfirmacaoEmailRepository confirmacaoEmailRepository;

    private final UsuarioRepository usuarioRepository;

    private final RequestCadastroDtoParaUsuarioConverter requestCadastroDtoParaUsuarioConverter;

    private final PasswordEncoder passwordEncoder;

    private final AppConfigurations appConfigurations;

    @Transactional
    public void cadastrar(RequestCadastro requestCadastro) throws UsuarioJaCadastradoException {

        if(this.usuarioRepository.existsByEmail(requestCadastro.getEmail())){
            throw new UsuarioJaCadastradoException("Usuário com o email informado já cadastrado!");
        }

        requestCadastro.setSenha(this.passwordEncoder.encode(requestCadastro.getSenha()));
        var usuario = this.requestCadastroDtoParaUsuarioConverter.converter(requestCadastro);

        this.usuarioRepository.save(usuario);

        var confirmacaoEmail = new ConfirmacaoEmail();
        confirmacaoEmail.setCodigoConfirmacao(String.valueOf(this.gerarChaveAleatoria()));
        confirmacaoEmail.setUsuario(usuario);
        confirmacaoEmail.setDataExpiracao(this.gerarDataExpiracao());

        this.confirmacaoEmailRepository.save(confirmacaoEmail);

        this.emailService.enviarEmail(usuario.getEmail(), "Confirmação de email", String.format("Prezado %s, a sua chave de confirmação para a Mini Rede Social é: %s", usuario.getNome(), confirmacaoEmail.getCodigoConfirmacao()));
    }

    @Transactional
    public void confirmarEmail(RequestConfirmarEmail requestConfirmarEmail) throws Exception {
        
        var usuarioOpt = this.usuarioRepository.findByEmail(requestConfirmarEmail.getEmail());

        if(usuarioOpt.isEmpty()) {
            throw new UsuarioNaoCadastradoException("Não encontrado usuário com o email informado.");
        }

        var usuario = usuarioOpt.get();

        if(usuario.isEmailConfirmado()) {
            throw new EmailJaConfirmadoException("O email informado já está confirmado.");
        }

        var confirmacaoEmailOpt = this.confirmacaoEmailRepository.findByCodigoConfirmacaoAndUsuario(requestConfirmarEmail.getCodigoConfirmacao(), usuario);

        if(confirmacaoEmailOpt.isEmpty()) {
            throw new ConfirmacaoNaoEncontradaException("Código de confirmação fornecido inválido para o email informado.");
        }

        var confirmacaoEmail = confirmacaoEmailOpt.get();

        if(confirmacaoEmail.getDataExpiracao().before(new Date())) {
            throw new CodigoConfirmacaoExpiradoException("Código de confirmação expirado.");
        }

        usuario.setEmailConfirmado(true);

        this.usuarioRepository.save(usuario);

    }

    @Transactional
    public void reenviarEmailConfirmacao(RequestReenviarEmailConfirmacao requestReenviarEmailConfirmacao) throws Exception {

        var usuarioOpt = this.usuarioRepository.findByEmail(requestReenviarEmailConfirmacao.getEmail());

        if(usuarioOpt.isEmpty()) {
            throw new UsuarioNaoCadastradoException("Não encontrado usuário com o email informado.");
        }

        var usuario = usuarioOpt.get();

        if(usuario.isEmailConfirmado()) {
            throw new EmailJaConfirmadoException("O email informado já está confirmado.");
        }

        var confirmacaoEmail = new ConfirmacaoEmail();
        confirmacaoEmail.setCodigoConfirmacao(String.valueOf(this.gerarChaveAleatoria()));
        confirmacaoEmail.setUsuario(usuario);
        confirmacaoEmail.setDataExpiracao(this.gerarDataExpiracao());

        this.confirmacaoEmailRepository.save(confirmacaoEmail);

        this.emailService.enviarEmail(usuario.getEmail(), "Confirmação de email", String.format("Prezado %s, a sua chave de confirmação para a Mini Rede Social é: %s", usuario.getNome(), confirmacaoEmail.getCodigoConfirmacao()));

    }

    @Transactional
    public void alterarDadosCadastrais(RequestAlterarDadosCadastrais requestAlterarDadosCadastrais, Usuario usuario) {
        
        if(Optional.ofNullable(requestAlterarDadosCadastrais.getFoto()).isPresent()) {
            usuario.setFoto(requestAlterarDadosCadastrais.getFoto());
        }

        if(Optional.ofNullable(requestAlterarDadosCadastrais.getResumo()).isPresent()) {
            usuario.setResumo(requestAlterarDadosCadastrais.getResumo());
        }

        if(Optional.ofNullable(requestAlterarDadosCadastrais.getDataNascimento()).isPresent()) {
            usuario.setDataNascimento(requestAlterarDadosCadastrais.getDataNascimento());
        }

        if(Optional.ofNullable(requestAlterarDadosCadastrais.getNome()).isPresent()) {
            usuario.setNome(requestAlterarDadosCadastrais.getNome());
        }

        if(Optional.ofNullable(requestAlterarDadosCadastrais.getSenha()).isPresent()) {
            usuario.setSenha(this.passwordEncoder.encode(requestAlterarDadosCadastrais.getSenha()));
        }

        if(Optional.ofNullable(requestAlterarDadosCadastrais.getLatitude()).isPresent()) {
            usuario.setLatitude(requestAlterarDadosCadastrais.getLatitude());
        }

        if(Optional.ofNullable(requestAlterarDadosCadastrais.getLongitude()).isPresent()) {
            usuario.setLongitude(requestAlterarDadosCadastrais.getLongitude());
        }

        this.usuarioRepository.save(usuario);

    }

    private int gerarChaveAleatoria() {
        return new SecureRandom().nextInt(900000) + 100000 ;
    }

    private Date gerarDataExpiracao() {
        var calendario = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendario.add(Calendar.MINUTE, this.appConfigurations.getMinutosParaEnviarCodigoConfirmacao());

        return calendario.getTime();
    }

}
