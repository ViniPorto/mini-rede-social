package com.univille.mini_rede_social.postagem.dto.output;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.univille.mini_rede_social.login.dto.output.ResponseUsuarioDto;
import com.univille.mini_rede_social.postagem.models.Postagem;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponsePostagemDto {
    
    private Long codigo;
    private String texto;
    private String imagem;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Sao_Paulo")
    private Date dataCriacao;
    private ResponseUsuarioDto usuario;
    private Integer curtidas;
    private Integer comentarios;

    public ResponsePostagemDto(Postagem postagem) {
        this.codigo = postagem.getCodigo();
        this.texto = postagem.getTexto();
        this.imagem = postagem.getImagem();
        this.dataCriacao = postagem.getDataCriacao();
        this.curtidas = postagem.getCurtidas();
        this.comentarios = postagem.getComentarios();
        this.usuario = new ResponseUsuarioDto(postagem.getUsuario());
    }

}
