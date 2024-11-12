package com.univille.mini_rede_social.notificacao.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.notificacao.models.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    Page<Notificacao> findAllByUsuarioAndLidaFalseOrderByDataCriacaoDesc(Pageable pageable, Usuario usuario);

    Page<Notificacao> findAllByUsuarioOrderByDataCriacaoDesc(Pageable pageable, Usuario usuario);

    Optional<Notificacao> findByCodigoAndUsuario(Long codigo, Usuario usuario);
    
}
