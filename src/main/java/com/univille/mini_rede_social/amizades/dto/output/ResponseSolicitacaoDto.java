package com.univille.mini_rede_social.amizades.dto.output;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.univille.mini_rede_social.amizades.models.Solicitacao;
import com.univille.mini_rede_social.login.dto.output.ResponseUsuarioDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseSolicitacaoDto {
    
    private Long codigo;
    private ResponseUsuarioDto usuarioRemetente;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Sao_Paulo")
    private Date data;

    public ResponseSolicitacaoDto(Solicitacao solicitacao) {
        this.codigo = solicitacao.getCodigo();
        this.usuarioRemetente = new ResponseUsuarioDto(solicitacao.getUsuarioRemetente());
        this.data = solicitacao.getData();
    }

}
