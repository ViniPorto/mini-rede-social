package com.univille.mini_rede_social.infra.security.exceptions;

public class TokenJWTInvalidoOuExpiradoException extends Exception {
    
    public TokenJWTInvalidoOuExpiradoException(String message) {
        super(message);
    }

}
