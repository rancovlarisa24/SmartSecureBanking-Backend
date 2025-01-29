package com.lrs.SSB.controller;

import com.lrs.SSB.service.EmailService;
import com.lrs.SSB.service.FirebaseSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    @Autowired
    private FirebaseSmsService firebaseSmsService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/phone")
    public ResponseEntity<?> sendPhoneCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        try {
            firebaseSmsService.sendVerificationCode(phone);
            return ResponseEntity.ok(Map.of("message", "SMS trimis cu succes."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Eroare la trimiterea SMS-ului: " + e.getMessage()));
        }
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = generateCode();
        try {
            emailService.sendVerificationEmail(email, code);
            return ResponseEntity.ok(Map.of("message", "Email trimis cu succes."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Eroare la trimiterea emailului: " + e.getMessage()));
        }
    }

    private String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
