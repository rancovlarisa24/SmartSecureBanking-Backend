package com.lrs.SSB.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    public void saveCode(String identifier, String code) {
        verificationCodes.put(identifier, code);
    }

    public boolean verifyCode(String identifier, String code) {
        String storedCode = verificationCodes.get(identifier);
        return (storedCode != null && storedCode.equals(code));
    }

    public void removeCode(String identifier) {
        verificationCodes.remove(identifier);
    }

    public String generateCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
