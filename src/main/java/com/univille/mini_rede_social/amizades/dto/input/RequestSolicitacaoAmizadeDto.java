package com.univille.mini_rede_social.amizades.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestSolicitacaoAmizadeDto {
    
    @NotNull
    Long codigoUsuarioAdicionar;

}
