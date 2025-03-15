package com.lrs.SSB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;

@Configuration
public class BlockchainCredentialsConfig {
    @Bean
    public Credentials credentials() {
        String privateKey = "0x567920a76ad968f7863dd466af4d58a076064f8bcfbe9e82328ee72ba3f498f1";
        Credentials credentials = Credentials.create(privateKey);
        System.out.println("Account address: " + credentials.getAddress());
        return credentials;
    }
}
