package com.univille.mini_rede_social.postagem.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.univille.mini_rede_social.postagem.dto.output.ResponsePostagemDto;
import com.univille.mini_rede_social.postagem.models.Postagem;

public interface PostagemRepository extends JpaRepository<Postagem, Long> {

    Optional<Postagem> findByCodigo(Long codigoPostagem);

    @Query(value = """
                    SELECT p
                    FROM PostagemComentario pc
                    JOIN pc.postagem p
                    WHERE p.codigo = :codigoPostagem
                    AND pc.codigo = :codigoComentario
                    """)
    Optional<Postagem> findByCodigoAndCodigoComentario(@Param("codigoPostagem") Long codigoPostagem, @Param("codigoComentario") Long codigoComentario);

    @Query(value = """
            SELECT new com.univille.mini_rede_social.postagem.dto.output.ResponsePostagemDto(p)
            FROM Postagem p
            WHERE p.usuario.codigo IN (SELECT a.usuarioAmigo.codigo FROM Amizade a WHERE a.usuarioPrincipal.codigo = :codigoUsuario)
            ORDER BY p.dataCriacao DESC
            """)
    Page<ResponsePostagemDto> listarTodasPostagensDeAmigosOrdenandoPorData(Pageable pageable, @Param("codigoUsuario") Long codigoUsuario);

    @Query(value = """
            SELECT new com.univille.mini_rede_social.postagem.dto.output.ResponsePostagemDto(p)
            FROM Postagem p
            WHERE p.usuario.codigo = :codigoUsuario
            ORDER BY p.dataCriacao DESC
            """)
    Page<ResponsePostagemDto> listarTodasPostagensDeUmUsuarioOrdenandoPorData(PageRequest pageable, @Param("codigoUsuario") Long codigoUsuario);
    
}
