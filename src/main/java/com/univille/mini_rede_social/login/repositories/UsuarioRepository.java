package com.univille.mini_rede_social.login.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.univille.mini_rede_social.login.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM Usuario u WHERE u.nome LIKE %:nome%
            """)
    Page<Usuario> findAllByNomeLike(PageRequest pageable, @Param("nome") String nome);
    
}
