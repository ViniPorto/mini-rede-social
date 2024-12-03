package com.univille.mini_rede_social.amizades.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.univille.mini_rede_social.amizades.dto.output.ResponseConferirUsuarioAmigoDto;
import com.univille.mini_rede_social.amizades.exceptions.AmizadeNaoEncontradaException;
import com.univille.mini_rede_social.amizades.repositories.AmizadeRepository;
import com.univille.mini_rede_social.cadastro.exceptions.UsuarioNaoCadastradoException;
import com.univille.mini_rede_social.infra.AppConfigurations;
import com.univille.mini_rede_social.login.dto.output.ResponseUsuarioDto;
import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.login.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AmizadeService {

    private final AppConfigurations appConfigurations;
    
    private final AmizadeRepository amizadeRepository;

    private final UsuarioRepository usuarioRepository;

    public Page<ResponseUsuarioDto> listarTodas(int page, int size, Usuario usuario) {
        var pageable = PageRequest.of(page, size);
        var amizades = this.amizadeRepository.listarTodasAmizades(pageable, usuario.getCodigo());
        
        return amizades.map(ResponseUsuarioDto::new);
    }

    public List<ResponseUsuarioDto> listarSugeridos(Usuario usuario) {
        var amigosDeAmigos = this.amizadeRepository.listarAmigosDeAmigosAleatorios(usuario.getCodigo(),
                                                                                   PageRequest.of(0, this.appConfigurations.getLimiteSugestaoAmigosAmigos()));
        var pessoasProximas = this.amizadeRepository.listarPessoasProximasAleatorias(this.appConfigurations.getRaioEmKmSugestaoAmizade(), 
                                                                                     usuario.getLatitude(), 
                                                                                     usuario.getLongitude(),
                                                                                     PageRequest.of(0, this.appConfigurations.getLimiteSugestaoPessoasProximas()),
                                                                                     usuario.getCodigo());

        //Utilizando set pois não permitem itens repetidos, esta é a forma que encontrei de unir as 2 listas de usuários 
        //sugeridos sem que os itens fossem repetidos
        var set = new HashSet<Usuario>();
        set.addAll(amigosDeAmigos);
        set.addAll(pessoasProximas);
        
        var listaRetorno = new ArrayList<Usuario>(set);

        Collections.shuffle(listaRetorno);

        return listaRetorno.stream().map(ResponseUsuarioDto::new).collect(Collectors.toList());
    }

    public Page<ResponseUsuarioDto> listarUsuariosPorNome(int page, int size, String nome) {
        var pageable = PageRequest.of(page, size);
        var usuarios = this.usuarioRepository.findAllByNomeLike(pageable, nome);

        return usuarios.map(ResponseUsuarioDto::new);
    }

    public ResponseUsuarioDto listarUsuarioPorId(Long id) throws UsuarioNaoCadastradoException {
        var usuarioOpt = this.usuarioRepository.findById(id);

        if(usuarioOpt.isEmpty()) {
            throw new UsuarioNaoCadastradoException("Não encontrado usuário com o código fornecido");
        }

        var usuario = usuarioOpt.get();

        return new ResponseUsuarioDto(usuario);
    }

    public ResponseConferirUsuarioAmigoDto conferirSeEhAmigo(Long id, Usuario usuario) throws UsuarioNaoCadastradoException {
        var usuarioOpt = this.usuarioRepository.findById(id);

        if(usuarioOpt.isEmpty()) {
            throw new UsuarioNaoCadastradoException("Não encontrado usuário com o código fornecido");
        }

        var usuarioAmigo = usuarioOpt.get();

        var ehAmigo = this.amizadeRepository.existsByUsuarioPrincipalAndUsuarioAmigo(usuarioAmigo, usuario);

        return new ResponseConferirUsuarioAmigoDto(ehAmigo);
    }

    public Page<ResponseUsuarioDto> listarAmigosDeUmUsuario(int page, int size, Long id) {
        var pageable = PageRequest.of(page, size);
        var amigos = this.amizadeRepository.listarTodasAmizades(pageable, id);

        return amigos.map(ResponseUsuarioDto::new);
    }

    @Transactional
    public void desamigar(Long codigoUsuarioDesamigar, Usuario usuario) throws UsuarioNaoCadastradoException, AmizadeNaoEncontradaException {
        var usuarioOpt = this.usuarioRepository.findById(codigoUsuarioDesamigar);

        if(usuarioOpt.isEmpty()) {
            throw new UsuarioNaoCadastradoException("Não encontrado usuário com o código fornecido");
        }

        var usuarioAmigo = usuarioOpt.get();

        var amizadeLado1opt = this.amizadeRepository.findByUsuarioPrincipalAndUsuarioAmigo(usuarioAmigo, usuario);
        var amizadeLado2opt = this.amizadeRepository.findByUsuarioPrincipalAndUsuarioAmigo(usuario, usuarioAmigo);

        if(amizadeLado1opt.isEmpty() && amizadeLado2opt.isEmpty()) {
            throw new AmizadeNaoEncontradaException("Não encontrado amizade com o usuário informado");
        }

        var amizadeLado1 = amizadeLado1opt.get();
        var amizadeLado2 = amizadeLado2opt.get();

        this.amizadeRepository.delete(amizadeLado1);
        this.amizadeRepository.delete(amizadeLado2);
    }

}
