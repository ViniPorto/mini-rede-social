package com.univille.mini_rede_social.email.services;

import com.univille.mini_rede_social.infra.AppConfigurations;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    
    private final AppConfigurations appConfigurations;

    public void enviarEmail(String destinatario, String assunto, String texto){
        var message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject(assunto);
        message.setText(texto);
        message.setFrom(this.appConfigurations.getEmailAccount());
        emailSender.send(message);
    }

}
