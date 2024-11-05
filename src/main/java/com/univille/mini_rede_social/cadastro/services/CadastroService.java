package com.univille.mini_rede_social.cadastro.services;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.univille.mini_rede_social.cadastro.converters.RequestCadastroDtoParaUsuarioConverter;
import com.univille.mini_rede_social.cadastro.dto.input.RequestCadastro;
import com.univille.mini_rede_social.cadastro.exceptions.UsuarioJaCadastradoException;
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

    private int gerarChaveAleatoria() {
        return new SecureRandom().nextInt(900000) + 100000 ;
    }

    private Date gerarDataExpiracao() {
        var calendario = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
        calendario.add(Calendar.MINUTE, this.appConfigurations.getMinutosParaEnviarCodigoConfirmacao());

        return calendario.getTime();
    }

}
