package com.lrs.SSB.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService {

    private static final String ACCOUNT_SID = "AC72de6c77fe23e2381576e08e8629e935";
    private static final String AUTH_TOKEN = "b8f6cd32a3095df8fcf042c54a6982c8";
    private static final String TWILIO_PHONE_NUMBER = "+16292763279";

    private final VerificationService verificationService;

    @Autowired
    public TwilioSmsService(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    public void sendVerificationCode(String toPhoneNumber) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        String code = verificationService.generateCode();
        verificationService.saveCode(toPhoneNumber, code);

        String smsContent = "Your verification code is: " + code;

        Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                smsContent
        ).create();
    }
}
