package com.univille.mini_rede_social.login.dto.input;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestLoginDto {
    
    @NotBlank
    String email;

    @NotBlank
    String senha;

}
