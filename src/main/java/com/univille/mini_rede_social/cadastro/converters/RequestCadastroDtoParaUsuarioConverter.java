package com.univille.mini_rede_social.cadastro.converters;

import org.springframework.stereotype.Component;

import com.univille.mini_rede_social.cadastro.dto.input.RequestCadastro;
import com.univille.mini_rede_social.login.models.Usuario;

@Component
public class RequestCadastroDtoParaUsuarioConverter {
    
    public Usuario converter(RequestCadastro requestCadastro) {
        Usuario usuario = new Usuario();
        usuario.setEmail(requestCadastro.getEmail());
        usuario.setSenha(requestCadastro.getSenha());
        usuario.setNome(requestCadastro.getNomeUsuario());
        usuario.setResumo(requestCadastro.getResumoPerfil());
        usuario.setDataNascimento(requestCadastro.getDataNascimento());
        usuario.setFoto(requestCadastro.getFotoPerfil());
        usuario.setLatitude(requestCadastro.getLatitude());
        usuario.setLongitude(requestCadastro.getLongitude());
        usuario.setEmailConfirmado(false);

        return usuario;
    }

}
