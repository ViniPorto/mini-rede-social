package com.univille.mini_rede_social.postagem.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.postagem.dto.input.RequestCriarComentario;
import com.univille.mini_rede_social.postagem.dto.input.RequestCriarPostagemDto;
import com.univille.mini_rede_social.postagem.services.PostagemService;
import com.univille.mini_rede_social.utils.ResponseHandler;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/postagem")
@AllArgsConstructor
public class PostagemController {

    private final ResponseHandler responseHandler;

    private final PostagemService postagemService;
    
    @PostMapping
    public ResponseEntity<?> criarPostagem(@RequestBody @Valid RequestCriarPostagemDto requestCriarPostagemDto, @AuthenticationPrincipal Usuario usuario) {
        try {
            this.postagemService.criarPostagem(requestCriarPostagemDto, usuario);
            return this.responseHandler.generateResponse("Postagem criada com sucesso", true, HttpStatus.CREATED, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Ocorreu um erro ao criar a postagem: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @PostMapping("/{codigoPostagem}/curtida")
    public ResponseEntity<?> curtirOuDescurtirPostagem(@PathVariable Long codigoPostagem, @AuthenticationPrincipal Usuario usuario) {
        try {
            this.postagemService.curtirOuDescurtirPostagem(codigoPostagem, usuario);
            return this.responseHandler.generateResponse("Curtido ou descurtido postagem com sucesso", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Ocorreu um erro ao curtir a postagem: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @PostMapping("/comentario")
    public ResponseEntity<?> comentarPostagem(@RequestBody @Valid RequestCriarComentario requestCriarComentario, @AuthenticationPrincipal Usuario usuario) {
        try {
            this.postagemService.criarComentario(requestCriarComentario, usuario);
            return this.responseHandler.generateResponse("Comentário criado com sucesso", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Ocorreu um erro ao criar comentário: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @PostMapping("/comentario/{codigoComentario}/curtida")
    public ResponseEntity<?> curtirOuDescurtirComentario(@PathVariable Long codigoComentario, @AuthenticationPrincipal Usuario usuario) {
        try {
            this.postagemService.curtirOuDescurtirComentario(codigoComentario, usuario);
            return this.responseHandler.generateResponse("Curtido comentário com sucesso", true, HttpStatus.OK, null);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Ocorreu um erro ao curtir o comentário: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("/todas")
    public ResponseEntity<?> listarTodasPostagens(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @AuthenticationPrincipal Usuario usuario) {
        try {
            var postagens = this.postagemService.listarTodasPostagens(page, size, usuario);
            return this.responseHandler.generateResponse("Listado postagens com sucesso", true, HttpStatus.OK, postagens);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Ocorreu um erro ao listar postagens: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("{codigoPostagem}/comentarios")
    public ResponseEntity<?> listarComentarios(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @PathVariable Long codigoPostagem) {
        try {
            var comentarios = this.postagemService.listarComentarios(page, size, codigoPostagem);
            return this.responseHandler.generateResponse("Listado comentários com sucesso", true, HttpStatus.OK, comentarios);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Ocorreu um erro ao listar comentários: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping("usuario/{codigoUsuario}")
    public ResponseEntity<?> listarPostagensDeUmUsuario(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @PathVariable Long codigoUsuario) {
        try {
            var postagens = this.postagemService.listarPostagensDeUmUsuario(page, size, codigoUsuario);
            return this.responseHandler.generateResponse("Listado postagens com sucesso", true, HttpStatus.OK, postagens);
        } catch (Exception e) {
            return this.responseHandler.generateResponse(String.format("Ocorreu um erro ao listar as postagens: %s", e.getMessage()), false, HttpStatus.BAD_REQUEST, null);
        }
    }

}
