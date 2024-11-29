package com.univille.mini_rede_social.postagem.models;

import java.util.Date;
import java.util.List;

import com.univille.mini_rede_social.login.models.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "T_POSTAGEM_COMENTARIO")
@Entity(name = "PostagemComentario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codigo")
public class PostagemComentario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PCO_CODIGO")
    private Long codigo;

    @ManyToOne
    @JoinColumn(name = "POS_CODIGO")
    private Postagem postagem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_CODIGO")
    private Usuario usuario;

    @Column(name = "PCO_DATA_CRIACAO")
    private Date dataCriacao;

    @Column(name = "PCO_TEXTO")
    private String texto;

    @Column(name = "PCO_FOTO")
    private String foto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PCO_CODIGO_PAI")
    private PostagemComentario comentarioPai;

    @OneToMany(mappedBy = "comentarioPai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostagemComentario> respostasComentarios;

    @Column(name = "PCO_CURTIDAS")
    private Integer curtidas;

    @Column(name = "PCO_RESPOSTAS")
    private Integer respostas;

    public void adicionarCurtida() {
        this.curtidas++;
    }

    public void retirarCurtida() {
        this.curtidas -= 1;
    }

    public void adicionarResposta() {
        this.respostas++;
    }

}
