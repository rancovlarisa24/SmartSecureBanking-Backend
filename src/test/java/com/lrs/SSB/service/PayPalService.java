package com.lrs.SSB.service;

import com.lrs.SSB.controller.CardDto;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class PayPalService {

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.clientSecret}")
    private String clientSecret;

    /**
     * Obține un Access Token din PayPal (Sandbox).
     */
    public String getAccessToken() throws IOException, ParseException {
        // 1. Construiești cererea
        URL url = new URL("https://api-m.sandbox.paypal.com/v1/oauth2/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // 2. Header pentru Basic Auth
        String creds = clientId + ":" + clientSecret;
        String encodedCreds = Base64.getEncoder().encodeToString(creds.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedCreds);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // 3. Body -> grant_type=client_credentials
        try (OutputStream os = conn.getOutputStream()) {
            String body = "grant_type=client_credentials";
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        // 4. Citești răspunsul din InputStream
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        // 5. Parsezi JSON folosind net.minidev.json.parser.JSONParser
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject json = (JSONObject) parser.parse(response.toString());

        // 6. Extragi access_token
        String accessToken = json.getAsString("access_token");
        return accessToken;
    }

    /**
     * Metodă care trimite datele cardului la PayPal Vault API
     * pentru a valida dacă este un card real.
     */
    public boolean validateCard(CardDto card) {
        try {
            // Obținem token
            String accessToken = getAccessToken();

            // Parse expiry date -> "03/2030" => "03" și "2030"
            String[] parts = card.getExpiryDate().split("/");
            String expireMonth = parts[0];
            String expireYear = parts[1];

            // Construiești cererea către PayPal Vault API
            URL url = new URL("https://api-m.sandbox.paypal.com/v1/vault/credit-cards");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");

            // Body JSON
            JSONObject body = new JSONObject();
            body.put("number", card.getCardNumber());
            body.put("type", deduceCardType(card.getCardNumber())); // ex: "visa"
            body.put("expire_month", expireMonth);
            body.put("expire_year", expireYear);
            body.put("cvv2", card.getCvv());
            // dacă vrei, poți adăuga: body.put("first_name", ...), etc.

            // Trimite la PayPal
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            // Răspuns
            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                // 200 OK / 201 Created => card valid
                return true;
            } else {
                // Mesaj de eroare
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder resp = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    resp.append(line);
                }
                br.close();
                System.out.println("PayPal error: " + resp);
                return false;
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Determină tipul cardului (visa, mastercard, etc.) în funcție de primele cifre.
     */
    private String deduceCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "visa";
        } else if (cardNumber.startsWith("5")) {
            return "mastercard";
        } else {
            return "unknown";
        }
    }
}
