package com.univille.mini_rede_social.amizades.models;

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

@Table(name = "T_SOLICITACAO")
@Entity(name = "Solicitacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codigo")
public class Solicitacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SOL_CODIGO")
    private Long codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_CODIGO_REMETENTE")
    private Usuario usuarioRemetente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_CODIGO_DESTINATARIO")
    private Usuario usuarioDestinatario;

    @Column(name = "SOL_DATA")
    private Date data;

}
