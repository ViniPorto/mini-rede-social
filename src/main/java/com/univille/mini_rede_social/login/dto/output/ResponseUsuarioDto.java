package com.univille.mini_rede_social.login.dto.output;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.univille.mini_rede_social.login.models.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseUsuarioDto {
    
    private Long codigo;
    private String email;
    private String nome;
    private String foto;
    private String resumo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataNascimento;

    public ResponseUsuarioDto(Usuario usuario) {
        this.codigo = usuario.getCodigo();
        this.email = usuario.getEmail();
        this.nome = usuario.getNome();
        this.foto = usuario.getFoto();
        this.resumo = usuario.getResumo();
        this.dataNascimento = usuario.getDataNascimento();
    }

}
