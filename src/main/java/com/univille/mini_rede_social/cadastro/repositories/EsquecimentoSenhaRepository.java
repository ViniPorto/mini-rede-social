package com.univille.mini_rede_social.cadastro.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.univille.mini_rede_social.cadastro.models.EsquecimentoSenha;
import com.univille.mini_rede_social.login.models.Usuario;

public interface EsquecimentoSenhaRepository extends JpaRepository<EsquecimentoSenha, Long> {

    void deleteByUsuario(Usuario usuario);

    Optional<EsquecimentoSenha> findByCodigoConfirmacaoAndUsuario(String codigoConfirmacao, Usuario usuario);
    
}
