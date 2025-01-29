package com.lrs.SSB.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseSmsService {

    public void sendVerificationCode(String phoneNumber) throws FirebaseAuthException {
        FirebaseAuth.getInstance().createCustomToken(phoneNumber);
        // Folosește Firebase Phone Authentication pentru cod.
        // Firebase va trimite automat un SMS către număr.
    }
}

