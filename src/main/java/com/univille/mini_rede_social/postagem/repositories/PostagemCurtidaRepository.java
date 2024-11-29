package com.univille.mini_rede_social.postagem.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.postagem.models.Postagem;
import com.univille.mini_rede_social.postagem.models.PostagemCurtida;

public interface PostagemCurtidaRepository extends JpaRepository<PostagemCurtida, Long> {

    Optional<PostagemCurtida> findByPostagemAndUsuario(Postagem postagem, Usuario usuario);
    
}
