package com.univille.mini_rede_social.email.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.univille.mini_rede_social.infra.AppConfigurations;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    
    @Mock
    JavaMailSender emailSender;

    @Mock
    AppConfigurations appConfigurations;

    EmailService emailService;

    @BeforeEach
    void onBefore() {
        this.emailService = new EmailService(emailSender, appConfigurations);
    }

    @Test
    void deveEnviarEmailCorretamnte() {
        when(this.appConfigurations.getEmailAccount()).thenReturn("email@gmail.com");

        doNothing().when(this.emailSender).send(any(SimpleMailMessage.class));

        this.emailService.enviarEmail("email1@gmail.com", "teste", "teste");

        verify(this.emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}
