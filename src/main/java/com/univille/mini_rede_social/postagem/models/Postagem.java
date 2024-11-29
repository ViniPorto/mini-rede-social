package com.univille.mini_rede_social.postagem.models;

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

@Table(name = "T_POSTAGEM")
@Entity(name = "Postagem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codigo")
public class Postagem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POS_CODIGO")
    private Long codigo;

    @Column(name = "POS_TEXTO")
    private String texto;

    @Column(name = "POS_IMAGEM")
    private String imagem;

    @Column(name = "POS_DATA_CRIACAO")
    private Date dataCriacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_CODIGO")
    private Usuario usuario;

    @Column(name = "POS_CURTIDAS")
    private Integer curtidas;

    @Column(name = "POS_COMENTARIOS")
    private Integer comentarios;

    public void adicionarCurtida() {
        this.curtidas++;
    }

    public void retirarCurtida() {
        this.curtidas -= 1;
    }

    public void adicionarComentario() {
        this.comentarios++;
    }

}
