package com.univille.mini_rede_social.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class AppConfigurations {
    
    @Value("${MINI_REDE_SOCIAL_SECURITY_TOKEN_SECRET}")
    private String tokenSecret;

}