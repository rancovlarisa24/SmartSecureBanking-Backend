package com.lrs.SSB.controller;

import com.lrs.SSB.entity.User;
import com.lrs.SSB.service.EmailService;
import com.lrs.SSB.service.TwilioSmsService;
import com.lrs.SSB.service.UserService;
import com.lrs.SSB.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    @Autowired
    private TwilioSmsService twilioSmsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private UserService userService;

    @PostMapping("/phone")
    public ResponseEntity<?> sendPhoneCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        if (phone == null || phone.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required."));
        }

        try {
            twilioSmsService.sendVerificationCode(phone);
            return ResponseEntity.ok(Map.of("message", "SMS sent successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error sending the SMS: " + e.getMessage()));
        }
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmailCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required."));
        }

        try {
            emailService.sendVerificationEmail(email);
            return ResponseEntity.ok(Map.of("message", "Email sent successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error sending the Email: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String code = request.get("code");

        if (identifier == null || identifier.isBlank() || code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Both identifier and code are required."));
        }

        boolean isValid = verificationService.verifyCode(identifier, code);
        if (isValid) {
            verificationService.removeCode(identifier);
            return ResponseEntity.ok(Map.of("message", "Code verified successfully."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired code."));
        }
    }

    @PostMapping("/phonePassReset")
    public ResponseEntity<?> sendPasswordResetPhoneCode(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");

        if (phone == null || phone.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required."));
        }
        boolean userExists = userService.userExists(phone);
        if (!userExists) {
            return ResponseEntity.status(404).body(Map.of("error", "User with this phone number does not exist."));
        }
        try {
            twilioSmsService.sendVerificationCode(phone);
            return ResponseEntity.ok(Map.of("message", "Password reset SMS sent successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error sending the password reset SMS: " + e.getMessage()));
        }
    }

    @PostMapping("/emailPassReset")
    public ResponseEntity<?> sendPasswordResetEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required."));
        }
        boolean userExists = userService.userExists(email);
        if (!userExists) {
            return ResponseEntity.status(404).body(Map.of("error", "User with this email does not exist."));
        }
        try {
            emailService.sendVerificationEmail(email);
            return ResponseEntity.ok(Map.of("message", "Password reset email sent successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error sending the password reset email: " + e.getMessage()));
        }
    }
}
