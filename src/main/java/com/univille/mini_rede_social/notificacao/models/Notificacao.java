package com.univille.mini_rede_social.notificacao.models;

import java.util.Date;

import com.univille.mini_rede_social.login.models.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "T_NOTIFICACAO")
@Entity(name = "Notificacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codigo")
public class Notificacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOT_CODIGO")
    private Long codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_CODIGO")
    private Usuario usuario;

    @Column(name = "NOT_FOTO")
    private String foto;

    @Column(name = "NOT_DATA_CRIACAO")
    private Date dataCriacao;

    @Column(name = "NOT_DESCRICAO")
    private String descricao;

    @Column(name = "NOT_LIDA")
    private Boolean lida;

}
