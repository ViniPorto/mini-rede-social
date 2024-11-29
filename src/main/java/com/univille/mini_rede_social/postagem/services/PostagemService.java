package com.univille.mini_rede_social.postagem.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.notificacao.services.NotificacaoService;
import com.univille.mini_rede_social.postagem.dto.input.RequestCriarComentario;
import com.univille.mini_rede_social.postagem.dto.input.RequestCriarPostagemDto;
import com.univille.mini_rede_social.postagem.dto.output.ResponseComentarioDto;
import com.univille.mini_rede_social.postagem.dto.output.ResponsePostagemDto;
import com.univille.mini_rede_social.postagem.exceptions.ComentarioNaoEncontradoException;
import com.univille.mini_rede_social.postagem.exceptions.ImagemETextoNaoInformadosException;
import com.univille.mini_rede_social.postagem.exceptions.ParametrosInsuficientesComentarioException;
import com.univille.mini_rede_social.postagem.exceptions.PostagemNaoEncontradaException;
import com.univille.mini_rede_social.postagem.models.Postagem;
import com.univille.mini_rede_social.postagem.models.PostagemComentario;
import com.univille.mini_rede_social.postagem.models.PostagemComentarioCurtida;
import com.univille.mini_rede_social.postagem.models.PostagemCurtida;
import com.univille.mini_rede_social.postagem.repositories.PostagemComentarioCurtidaRepository;
import com.univille.mini_rede_social.postagem.repositories.PostagemComentarioRepository;
import com.univille.mini_rede_social.postagem.repositories.PostagemCurtidaRepository;
import com.univille.mini_rede_social.postagem.repositories.PostagemRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostagemService {
    
    private final PostagemRepository postagemRepository;

    private final PostagemCurtidaRepository postagemCurtidaRepository;

    private final PostagemComentarioRepository postagemComentarioRepository;

    private final PostagemComentarioCurtidaRepository postagemComentarioCurtidaRepository;

    private final NotificacaoService notificacaoService;

    @Transactional
    public void criarPostagem(RequestCriarPostagemDto requestCriarPostagemDto, Usuario usuario) throws ImagemETextoNaoInformadosException {
        if(Optional.ofNullable(requestCriarPostagemDto.getTexto()).isEmpty() && Optional.ofNullable(requestCriarPostagemDto.getImagem()).isEmpty()) {
            throw new ImagemETextoNaoInformadosException("Imagem e texto não foram informados, ao menos uma informação deve ser fornecida");
        }

        Postagem postagem = new Postagem();
        postagem.setImagem(requestCriarPostagemDto.getImagem());
        postagem.setTexto(requestCriarPostagemDto.getTexto());
        postagem.setDataCriacao(new Date());
        postagem.setUsuario(usuario);
        postagem.setCurtidas(0);
        postagem.setComentarios(0);

        this.postagemRepository.save(postagem);
    }

    @Transactional
    public void curtirOuDescurtirPostagem(Long codigoPostagem, Usuario usuario) throws PostagemNaoEncontradaException {
        var postagemOpt = this.postagemRepository.findByCodigo(codigoPostagem);

        if(postagemOpt.isEmpty()) {
            throw new PostagemNaoEncontradaException("Não encontrado postagem com o código fornecido");
        }

        var postagem = postagemOpt.get();

        var postagemCurtidaOpt = this.postagemCurtidaRepository.findByPostagemAndUsuario(postagem, usuario);

        //Se já existir registro, então é uma descurtida
        if(postagemCurtidaOpt.isEmpty()) {
            var postagemCurtida = new PostagemCurtida();
            postagemCurtida.setPostagem(postagem);
            postagemCurtida.setUsuario(usuario);
    
            this.postagemCurtidaRepository.save(postagemCurtida);

            postagem.adicionarCurtida();
            this.postagemRepository.save(postagem);
    
            this.notificacaoService.registrarNotificacao(postagem.getUsuario(), usuario.getFoto(), new Date(), String.format("%s curtiu sua postagem.", usuario.getNome()));
        } else {
            postagem.retirarCurtida();
            this.postagemRepository.save(postagem);

            this.postagemCurtidaRepository.delete(postagemCurtidaOpt.get());
        } 
    }

    @Transactional
    public void criarComentario(RequestCriarComentario requestCriarComentario, Usuario usuario) throws PostagemNaoEncontradaException, ParametrosInsuficientesComentarioException, ComentarioNaoEncontradoException {
        if(Optional.ofNullable(requestCriarComentario.getFoto()).isEmpty() && Optional.ofNullable(requestCriarComentario.getTexto()).isEmpty()) {
            throw new ParametrosInsuficientesComentarioException("É necessário informar texto ou foto");
        }

        Optional<Postagem> postagemOpt = null;

        if(Optional.ofNullable(requestCriarComentario.getCodigoComentarioPai()).isPresent()) {
            postagemOpt = this.postagemRepository.findByCodigoAndCodigoComentario(requestCriarComentario.getCodigoPostagem(), requestCriarComentario.getCodigoComentarioPai());
        } else {
            postagemOpt = this.postagemRepository.findByCodigo(requestCriarComentario.getCodigoPostagem());
        }
    
        if(postagemOpt.isEmpty()) {
            throw new PostagemNaoEncontradaException("Não encontrado postagem com o código fornecido");
        }

        var postagem = postagemOpt.get();

        var comentarioPaiOpt = this.postagemComentarioRepository.findByCodigo(requestCriarComentario.getCodigoComentarioPai());

        if(comentarioPaiOpt.isEmpty() && Optional.ofNullable(requestCriarComentario.getCodigoComentarioPai()).isPresent()) {
            throw new ComentarioNaoEncontradoException("Não encontrado comentário com o código fornecido");
        }

        var comentario = new PostagemComentario();
        comentario.setPostagem(postagem);
        comentario.setUsuario(usuario);
        comentario.setDataCriacao(new Date());
        comentario.setTexto(requestCriarComentario.getTexto());
        comentario.setFoto(requestCriarComentario.getFoto());
        comentario.setRespostas(0);
        comentario.setCurtidas(0);
        if(comentarioPaiOpt.isPresent()) {
            var comentarioPai = comentarioPaiOpt.get();
            comentario.setComentarioPai(comentarioPai);   
            comentarioPai.adicionarResposta();      
            this.postagemComentarioRepository.save(comentarioPai); 
        }

        this.postagemComentarioRepository.save(comentario);

        postagem.adicionarComentario();
        this.postagemRepository.save(postagem);

        var mensagem = Optional.ofNullable(requestCriarComentario.getCodigoComentarioPai()).isPresent() ? "%s respondeu ao seu comentário" : "%s comentou em sua postagem";

        this.notificacaoService.registrarNotificacao(postagem.getUsuario(), usuario.getFoto(), new Date(), String.format(mensagem, usuario.getNome()));
    }

    @Transactional
    public void curtirOuDescurtirComentario(Long codigoComentario, Usuario usuario) throws ComentarioNaoEncontradoException {
        var comentarioOpt = this.postagemComentarioRepository.findByCodigo(codigoComentario);

        if(comentarioOpt.isEmpty()) {
            throw new ComentarioNaoEncontradoException("Não encontrado comentário com o código informado");
        }
        
        var comentario = comentarioOpt.get();

        var comentarioCurtidaOpt = this.postagemComentarioCurtidaRepository.findByComentarioAndUsuario(comentario, usuario);

        //Se já existir registro, então é uma descurtida
        if(comentarioCurtidaOpt.isEmpty()) {
            var comentarioCurtida = new PostagemComentarioCurtida();
            comentarioCurtida.setComentario(comentario);
            comentarioCurtida.setUsuario(usuario);
    
            this.postagemComentarioCurtidaRepository.save(comentarioCurtida);

            comentario.adicionarCurtida();
            this.postagemComentarioRepository.save(comentario);
    
            this.notificacaoService.registrarNotificacao(comentario.getUsuario(), usuario.getFoto(), new Date(), String.format("%s curtiu seu comentário", usuario.getNome()));
        } else {
            comentario.retirarCurtida();
            this.postagemComentarioRepository.save(comentario);

            this.postagemComentarioCurtidaRepository.delete(comentarioCurtidaOpt.get());
        }
    }

    public Page<ResponsePostagemDto> listarTodasPostagens(int page, int size, Usuario usuario) {
        var pageable = PageRequest.of(page, size);

        return this.postagemRepository.listarTodasPostagensDeAmigosOrdenandoPorData(pageable, usuario.getCodigo());
    }

    public Page<ResponseComentarioDto> listarComentarios(int page, int size, Long codigoPostagem) throws PostagemNaoEncontradaException {
        var pageable = PageRequest.of(page, size);
        return this.postagemComentarioRepository.listarTodosComentariosOrdenandoPorNumeroRespostas(pageable, codigoPostagem);
    }

    public Page<ResponsePostagemDto> listarPostagensDeUmUsuario(int page, int size, Long codigoUsuario) {
        var pageable = PageRequest.of(page, size);
        
        return this.postagemRepository.listarTodasPostagensDeUmUsuarioOrdenandoPorData(pageable, codigoUsuario);
    }

}
