package com.univille.mini_rede_social.login.models;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "T_USUARIO")
@Entity(name = "Usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codigo")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USU_CODIGO")
    private Long codigo;

    @Column(name = "USU_EMAIL")
    private String email;

    @Column(name = "USU_SENHA")
    private String senha;

    @Column(name = "USU_EMAIL_CONFIRMADO")
    private boolean emailConfirmado;

    @Column(name = "USU_NOME")
    private String nome;

    @Column(name = "USU_FOTO")
    private String foto;

    @Column(name = "USU_RESUMO")
    private String resumo;

    @Column(name = "USU_DATA_NASCIMENTO")
    private Date dataNascimento;

    @Column(name = "USU_LATITUDE")
    private Double latitude;

    @Column(name = "USU_LONGITUDE")
    private Double longitude;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
