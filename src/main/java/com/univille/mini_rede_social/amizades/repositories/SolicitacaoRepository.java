package com.univille.mini_rede_social.amizades.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.univille.mini_rede_social.amizades.models.Solicitacao;
import com.univille.mini_rede_social.login.models.Usuario;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    boolean existsByUsuarioDestinatarioAndUsuarioRemetente(Usuario usuarioDestinatario, Usuario usuarioRemetente);

    Page<Solicitacao> findAllByUsuarioDestinatario(Pageable pageable, Usuario usuario);

    Optional<Solicitacao> findByCodigoAndUsuarioDestinatario(Long codigoSolicitacao, Usuario usuario);
    
}
