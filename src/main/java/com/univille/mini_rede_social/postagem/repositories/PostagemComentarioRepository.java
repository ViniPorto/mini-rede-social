package com.univille.mini_rede_social.postagem.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.univille.mini_rede_social.postagem.dto.output.ResponseComentarioDto;
import com.univille.mini_rede_social.postagem.models.PostagemComentario;

public interface PostagemComentarioRepository extends JpaRepository<PostagemComentario, Long> {

    Optional<PostagemComentario> findByCodigo(Long codigoComentarioPai);

    @Query(value = """
            SELECT new com.univille.mini_rede_social.postagem.dto.output.ResponseComentarioDto(pcm)
            FROM PostagemComentario pcm
            WHERE pcm.postagem.codigo = :codigoPostagem
            AND pcm.comentarioPai IS NULL
            ORDER BY pcm.respostas DESC
            """)
    Page<ResponseComentarioDto> listarTodosComentariosOrdenandoPorNumeroRespostas(Pageable pageable, @Param("codigoPostagem") Long codigoPostagem);
    
}
