package com.univille.mini_rede_social.cadastro.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.univille.mini_rede_social.cadastro.models.ConfirmacaoEmail;

public interface ConfirmacaoEmailRepository extends JpaRepository<ConfirmacaoEmail, Long> {
    
}
