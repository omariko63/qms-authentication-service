package com.qualitymanagementsystem.auth_service.service;

import com.qualitymanagementsystem.auth_service.config.MailConfig;
import com.qualitymanagementsystem.auth_service.dto.MailBody;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    /**
     * Class Which is Responsiable for Actually Sending the mail
     */
    private final JavaMailSender javaMailSender;

    private final MailConfig mailConfig;

    @Async
    public void sendSimpleMessage(MailBody mailBody){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom(mailConfig.getFrom());
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        javaMailSender.send(message);
    }
}
