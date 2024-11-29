package com.univille.mini_rede_social.postagem.dto.output;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.univille.mini_rede_social.login.dto.output.ResponseUsuarioDto;
import com.univille.mini_rede_social.postagem.models.PostagemComentario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseComentarioDto {

    private Long codigo;
    private ResponseUsuarioDto usuario;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Sao_Paulo")
    private Date dataCriacao;
    private String texto;
    private String foto;
    private Integer curtidas;
    private Integer respostas;
    private List<ResponseComentarioDto> comentariosRespostas;

    public ResponseComentarioDto(PostagemComentario postagemComentario) {
        this.codigo = postagemComentario.getCodigo();
        this.usuario = new ResponseUsuarioDto(postagemComentario.getUsuario());
        this.dataCriacao = postagemComentario.getDataCriacao();
        this.texto = postagemComentario.getTexto();
        this.foto = postagemComentario.getFoto();
        this.curtidas = postagemComentario.getCurtidas();
        this.respostas = postagemComentario.getRespostas();
        this.comentariosRespostas = postagemComentario.getRespostasComentarios().stream().map(ResponseComentarioDto::new).collect(Collectors.toList());
    }

}
