package com.univille.mini_rede_social.cadastro.dto.input;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAlterarDadosCadastrais {
    
    String foto;

    String resumo;

    Date dataNascimento;

    String nome;

    String senha;

    Double latitude;

    Double longitude;

}
