package com.lrs.SSB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;

@Configuration
public class BlockchainCredentialsConfig {
    public Credentials getCredentialsFromPrivateKey(String privateKey) {
        return Credentials.create(privateKey);
    }
}
