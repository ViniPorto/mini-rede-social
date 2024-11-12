package com.univille.mini_rede_social.notificacao.dto.output;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.univille.mini_rede_social.notificacao.models.Notificacao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseNotificacaoDto {
    
    private Long codigo;
    private Long usuarioCodigo;
    private String foto;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataCriacao;
    private String descricao;
    private Boolean lida;

    public ResponseNotificacaoDto(Notificacao notificacao) {
        this.codigo = notificacao.getCodigo();
        this.usuarioCodigo = notificacao.getUsuario().getCodigo();
        this.foto = notificacao.getFoto();
        this.dataCriacao = notificacao.getDataCriacao();
        this.descricao = notificacao.getDescricao();
        this.lida = notificacao.getLida();
    }

}
