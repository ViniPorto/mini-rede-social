package com.univille.mini_rede_social.cadastro.models;

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

@Table(name = "T_ESQUECIMENTO_SENHA")
@Entity(name = "EsquecimentoSenha")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codigo")
public class EsquecimentoSenha {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ESQ_CODIGO")
    private Long codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_CODIGO")
    private Usuario usuario;

    @Column(name = "ESQ_CODIGO_CONFIRMACAO")
    private String codigoConfirmacao;

    @Column(name = "ESQ_DATA_EXPIRACAO")
    private Date dataExpiracao;

    @Column(name = "ESQ_NOVA_SENHA")
    private String novaSenha;

}
