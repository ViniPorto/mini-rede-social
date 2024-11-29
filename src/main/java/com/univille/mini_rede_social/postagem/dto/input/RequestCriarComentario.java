package com.univille.mini_rede_social.postagem.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCriarComentario {
    
    @NotNull
    Long codigoPostagem;

    String texto;

    String foto;

    Long codigoComentarioPai;

}
