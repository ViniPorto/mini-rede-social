package com.univille.mini_rede_social.infra.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.univille.mini_rede_social.infra.security.services.AutenticacaoService;
import com.univille.mini_rede_social.login.models.Usuario;
import com.univille.mini_rede_social.login.repositories.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {
    
    @Mock
    UsuarioRepository usuarioRepository;

    AutenticacaoService autenticacaoService;

    @BeforeEach
    void onBefore() {
        this.autenticacaoService = new AutenticacaoService(usuarioRepository);
    }

    @Test
    void deveLancarUsernameNotFoundExceptionAoCarregarUsuarioPeloUsername() {
        Usuario usuario = null;
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            this.autenticacaoService.loadUserByUsername("email");
        });
    }

    @Test
    void deveRetornarUserDetailsAoCarregarUsuarioPeloUsername() {
        var usuario = new Usuario();
        var opt = Optional.ofNullable(usuario);

        when(this.usuarioRepository.findByEmail(anyString())).thenReturn(opt);

        var response = this.autenticacaoService.loadUserByUsername("email");

        Assertions.assertNotNull(response);
    }

}
