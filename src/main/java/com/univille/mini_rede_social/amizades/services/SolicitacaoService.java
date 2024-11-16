package com.univille.mini_rede_social.amizades.services;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.univille.mini_rede_social.amizades.dto.output.ResponseSolicitacaoDto;
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

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SolicitacaoService {
    
    private final SolicitacaoRepository solicitacaoRepository;

    private final UsuarioRepository usuarioRepository;

    private final AmizadeRepository amizadeRepository;

    private final NotificacaoService notificacaoService;

    @Transactional
    public void criarSolicitacao(Long codigoUsuarioAdicionar, Usuario usuarioRemetente) throws UsuarioNaoCadastradoException, UsuarioRepetidoException, SolicitacaoJaEnviadaExcetion, AmizadeJaExistenteException {
        var usuarioOpt = this.usuarioRepository.findById(codigoUsuarioAdicionar);

        if(usuarioOpt.isEmpty()) {
            throw new UsuarioNaoCadastradoException("Não encontrado usuário com o código fornecido");
        }

        var usuarioDestinatario = usuarioOpt.get();

        if(usuarioDestinatario.equals(usuarioRemetente)) {
            throw new UsuarioRepetidoException("O usuário a ser adicionado não pode ser o mesmo que está solicitando");
        }

        if(this.solicitacaoRepository.existsByUsuarioDestinatarioAndUsuarioRemetente(usuarioDestinatario, usuarioRemetente)) {
            throw new SolicitacaoJaEnviadaExcetion("Já enviado solicitação para o usuário");
        }

        if(this.amizadeRepository.existsByUsuarioPrincipalAndUsuarioAmigo(usuarioDestinatario, usuarioRemetente)) {
            throw new AmizadeJaExistenteException("Não é possível enviar solicitação para usuário que já foi adicionado");
        }

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setData(new Date());
        solicitacao.setUsuarioDestinatario(usuarioDestinatario);
        solicitacao.setUsuarioRemetente(usuarioRemetente);

        this.solicitacaoRepository.save(solicitacao);
    }

    public Page<ResponseSolicitacaoDto> listarTodas(int page, int size, Usuario usuario) {
        var pageable = PageRequest.of(page, size);
        var solicitacoes = this.solicitacaoRepository.findAllByUsuarioDestinatario(pageable, usuario);

        return solicitacoes.map(ResponseSolicitacaoDto::new);
    }

    @Transactional
    public void responderSolicitacao(Boolean aceitar, Long codigoSolicitacao, Usuario usuario) throws SolicitacaoNaoCadastradaException {   
        var solicitacaoOpt = this.solicitacaoRepository.findByCodigoAndUsuarioDestinatario(codigoSolicitacao, usuario);

        if(solicitacaoOpt.isEmpty()) {
            throw new SolicitacaoNaoCadastradaException("Não encontrado solicitação de amizade com o código fornecido");
        }

        var solicitacao = solicitacaoOpt.get();

        if(aceitar) {
            var amizade1 = new Amizade();
            var amizade2 = new Amizade();

            amizade1.setUsuarioPrincipal(usuario);
            amizade1.setUsuarioAmigo(solicitacao.getUsuarioRemetente());
            amizade1.setData(new Date());

            amizade2.setUsuarioPrincipal(solicitacao.getUsuarioRemetente());
            amizade2.setUsuarioAmigo(usuario);
            amizade2.setData(new Date());

            this.amizadeRepository.save(amizade1);
            this.amizadeRepository.save(amizade2);
        }

        this.solicitacaoRepository.delete(solicitacao);

        this.notificacaoService.registrarNotificacao(solicitacao.getUsuarioRemetente(), usuario.getFoto(), new Date(), String.format("%s aceitou sua solicitação de amizade!", usuario.getNome()));
    }

}
