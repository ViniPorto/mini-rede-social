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

@Table(name = "T_AMIZADE")
@Entity(name = "Amizade")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "codigo")
public class Amizade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AMI_CODIGO")
    private Long codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_CODIGO_PRINCIPAL")
    private Usuario usuarioPrincipal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USU_CODIGO_AMIGO")
    private Usuario usuarioAmigo;

    @Column(name = "AMI_DATA")
    private Date data;

}
