package com.univille.mini_rede_social.infra.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.univille.mini_rede_social.login.repositories.UsuarioRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = this.usuarioRepository.findByEmail(email);

        if(usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email fornecido");
        }
        
        return usuario.get();
    }
    
}
