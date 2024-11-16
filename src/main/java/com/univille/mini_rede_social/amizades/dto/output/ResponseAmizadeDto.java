package com.univille.mini_rede_social.amizades.dto.output;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.univille.mini_rede_social.amizades.models.Amizade;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseAmizadeDto {
    
    private Long codigo;
    private Long usuarioPrincipalCodigo;
    private Long usuarioAmigoCodigo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date data;

    public ResponseAmizadeDto(Amizade amizade) {
        this.codigo = amizade.getCodigo();
        this.usuarioPrincipalCodigo = amizade.getUsuarioPrincipal().getCodigo();
        this.usuarioAmigoCodigo = amizade.getUsuarioAmigo().getCodigo();
        this.data = amizade.getData();
    }

}
