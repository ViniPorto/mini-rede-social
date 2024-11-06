package com.univille.mini_rede_social.cadastro.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.univille.mini_rede_social.cadastro.models.ConfirmacaoEmail;
import com.univille.mini_rede_social.login.models.Usuario;

public interface ConfirmacaoEmailRepository extends JpaRepository<ConfirmacaoEmail, Long> {

    Optional<ConfirmacaoEmail> findByCodigoConfirmacaoAndUsuario(String codigoConfirmacao, Usuario usuario);
    
}
