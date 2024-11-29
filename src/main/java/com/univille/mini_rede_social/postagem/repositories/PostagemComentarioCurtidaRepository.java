package com.univille.mini_rede_social.postagem.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.postagem.models.PostagemComentario;
import com.univille.mini_rede_social.postagem.models.PostagemComentarioCurtida;

public interface PostagemComentarioCurtidaRepository extends JpaRepository<PostagemComentarioCurtida, Long> {

    Optional<PostagemComentarioCurtida> findByComentarioAndUsuario(PostagemComentario comentario, Usuario usuario);
    
}
