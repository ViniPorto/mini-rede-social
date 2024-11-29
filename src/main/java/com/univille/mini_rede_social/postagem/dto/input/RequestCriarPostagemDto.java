package com.univille.mini_rede_social.postagem.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCriarPostagemDto {
    
    String texto;
    
    String imagem;

}
