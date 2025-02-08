package com.lrs.SSB.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VerificationService verificationService;

    public void sendVerificationEmail(String to) {
        String code = verificationService.generateCode();
        verificationService.saveCode(to, code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Cod de verificare");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }
}
