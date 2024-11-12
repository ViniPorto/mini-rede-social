package com.univille.mini_rede_social.notificacao.services;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.notificacao.dto.output.ResponseNotificacaoDto;
import com.univille.mini_rede_social.notificacao.exceptions.NotificacaoNaoEncontradaException;
import com.univille.mini_rede_social.notificacao.models.Notificacao;
import com.univille.mini_rede_social.notificacao.repositories.NotificacaoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public Page<ResponseNotificacaoDto> listarNovas(int page, int size, Usuario usuario) {
        var pageable = PageRequest.of(page, size);
        var notificacoes = this.notificacaoRepository.findAllByUsuarioAndLidaFalseOrderByDataCriacaoDesc(pageable, usuario);

        return notificacoes.map(ResponseNotificacaoDto::new);
    }

    public Page<ResponseNotificacaoDto> listarTodas(int page, int size, Usuario usuario) {
        var pageable = PageRequest.of(page, size);
        var notificacoes = this.notificacaoRepository.findAllByUsuarioOrderByDataCriacaoDesc(pageable, usuario);

        return notificacoes.map(ResponseNotificacaoDto::new);
    }

    public void confirmarLeitura(Long codigo, Usuario usuario) throws NotificacaoNaoEncontradaException {
        var notificacaoOpt = this.notificacaoRepository.findByCodigoAndUsuario(codigo, usuario);

        if(notificacaoOpt.isEmpty()) {
            throw new NotificacaoNaoEncontradaException("Não encontrado notificação para o usuário com o código informado");
        }

        var notificacao = notificacaoOpt.get();

        notificacao.setLida(true);

        this.notificacaoRepository.save(notificacao);
    }

    public void registrarNotificacao(Usuario usuario, String foto, Date dataCriacao, String descricao) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setFoto(foto);
        notificacao.setDataCriacao(dataCriacao);
        notificacao.setDescricao(descricao);
        notificacao.setLida(false);

        this.notificacaoRepository.save(notificacao);
    }
    
}
