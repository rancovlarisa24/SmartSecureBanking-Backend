package com.lrs.SSB.service;

import com.b.contract.TransactionValidator;
import com.lrs.SSB.BlockchainCredentialsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;
import java.math.BigInteger;

@Service
public class BlockchainService {

    @Autowired
    private Web3j web3j;

    @Autowired
    private BlockchainCredentialsConfig credentialsConfig;

    public boolean validateTransferOnBlockchain(String sourceId, String destId, BigInteger amount, String userPrivateKey) {
        try {
            String contractAddress = "0x8CB8F09B077217798F505C8d02702e2617f49898";

            Credentials userCredentials = credentialsConfig.getCredentialsFromPrivateKey(userPrivateKey);

            TransactionValidator contract = TransactionValidator.load(contractAddress, web3j, userCredentials, new DefaultGasProvider());

            String txIdString = sourceId + "-" + destId + "-" + amount+ "-" + System.currentTimeMillis();
            byte[] txHash = Numeric.hexStringToByteArray(org.web3j.crypto.Hash.sha3String(txIdString));
            TransactionReceipt receipt = contract.validateTransfer(sourceId, destId, amount, txHash).send();
            return receipt.getStatus() != null && receipt.getStatus().equals("0x1");

        } catch (Exception e) {
            System.err.println("Eroare la validare: " + e.getMessage());
            return false;
        }
    }
}
