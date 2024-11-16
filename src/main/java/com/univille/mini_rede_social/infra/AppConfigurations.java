package com.univille.mini_rede_social.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class AppConfigurations {
    
    @Value("${MINI_REDE_SOCIAL_SECURITY_TOKEN_SECRET}")
    private String tokenSecret;

    @Value("${MINI_REDE_SOCIAL_EMAIL_ACCOUNT}")
    private String emailAccount;

    @Value("${MINI_REDE_SOCIAL_MINUTOS_PARA_ENVIAR_CODIGO_CONFIRMACAO}")
    private Integer minutosParaEnviarCodigoConfirmacao;

    @Value("${MINI_REDE_SOCIAL_RAIO_EM_KM_SUGESTAO_AMIZADES}")
    private Integer raioEmKmSugestaoAmizade;

    @Value("${MINI_REDE_SOCIAL_LIMITE_SUGESTAO_AMIGOS_AMIGOS}")
    private Integer limiteSugestaoAmigosAmigos;

    @Value("${MINI_REDE_SOCIAL_LIMITE_SUGESTAO_PESSOAS_PROXIMAS}")
    private Integer limiteSugestaoPessoasProximas;
    
}
