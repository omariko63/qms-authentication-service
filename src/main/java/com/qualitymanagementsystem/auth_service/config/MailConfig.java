package com.qualitymanagementsystem.auth_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MailConfig {

    @Value("${app.mail.from}")
    private String from;
}
