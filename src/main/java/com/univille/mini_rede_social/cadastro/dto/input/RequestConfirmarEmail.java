package com.univille.mini_rede_social.cadastro.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestConfirmarEmail {
    
    @NotBlank
    @Email
    String email;

    @NotBlank
    String codigoConfirmacao;

}
