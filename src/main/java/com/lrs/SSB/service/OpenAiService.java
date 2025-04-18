package com.lrs.SSB.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import com.lrs.SSB.controller.ChatRequestDTO;
import com.lrs.SSB.controller.ChatResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Value("classpath:docs/*.txt") private Resource[] guideFiles;
    private static final int MAX_GUIDES = 3;
    private static final int MIN_TOKEN_LEN = 3;
    private static class GuideData {
        String filename;
        String content;
        Map<String, Double> tfidf;
        double norm;
    }

    private final List<GuideData> guides = new ArrayList<>();
    private final Map<String, Double> idf = new HashMap<>();

    private final WebClient webClient;

    public OpenAiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @PostConstruct
    private void init() {

        Map<String, Map<String, Integer>> termFreqPerGuide = new HashMap<>();
        for (Resource res : guideFiles) {
            try {
                String text = new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                String file = res.getFilename();
                Map<String, Integer> tf = tokenize(text);
                termFreqPerGuide.put(file, tf);

                GuideData gd = new GuideData();
                gd.filename = file;
                gd.content  = text;
                guides.add(gd);
            } catch (IOException e) {
                System.err.println("I can't read " + res.getFilename()); e.printStackTrace();
            }
        }

        int N = guides.size();
        Map<String, Integer> docFreq = new HashMap<>();
        termFreqPerGuide.values().forEach(tf -> tf.keySet().forEach(t -> docFreq.merge(t, 1, Integer::sum)));
        for (var e : docFreq.entrySet()) {
            idf.put(e.getKey(), Math.log((double) N / (e.getValue())));
        }

        for (GuideData gd : guides) {
            Map<String, Integer> tf = termFreqPerGuide.get(gd.filename);
            Map<String, Double> vec = new HashMap<>();
            double sumSq = 0;
            for (var e : tf.entrySet()) {
                double w = e.getValue() * idf.getOrDefault(e.getKey(), 0.0);
                if (w > 0) {
                    vec.put(e.getKey(), w);
                    sumSq += w * w;
                }
            }
            gd.tfidf = vec;
            gd.norm  = Math.sqrt(sumSq);
        }
        System.out.println("Guides loaded (TF‑IDF ready): " + guides.size());
    }

    public String askChatGPT(String userMessage) {
        String context = buildContext(userMessage);

        ChatRequestDTO.Message sys = new ChatRequestDTO.Message("system", """
            Ești Lary, asistentul AI pentru aplicația Smart Secure Banking.
            Dacă întrebarea este despre aplicație, folosește contextul de mai jos.
            Altfel, răspunde ca un asistent AI general.

            --------------- CONTEXT ---------------
            %s
            ----------------------------------------
            """.formatted(context));

        ChatRequestDTO.Message user = new ChatRequestDTO.Message("user", userMessage);

        ChatRequestDTO body = new ChatRequestDTO();
        body.setModel("gpt-4o");
        body.setMessages(List.of(sys, user));

        ChatResponseDTO resp = webClient.post()
                .uri(openAiApiUrl)
                .header("Authorization", "Bearer " + openAiApiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class)
                .block();

        return (resp != null && !resp.getChoices().isEmpty())
                ? resp.getChoices().get(0).getMessage().getContent()
                : "Lary couldn't generate a response.";
    }

    private String buildContext(String question) {

        Map<String, Integer> qTf = tokenize(question);
        Map<String, Double> qVec = new HashMap<>();
        double qNormSq = 0;
        for (var e : qTf.entrySet()) {
            double w = e.getValue() * idf.getOrDefault(e.getKey(), 0.0);
            if (w > 0) {
                qVec.put(e.getKey(), w);
                qNormSq += w * w;
            }
        }
        double qNorm = Math.sqrt(qNormSq == 0 ? 1 : qNormSq);
        record Scored(GuideData g, double sim) {}
        List<Scored> ranked = guides.stream()
                .map(g -> new Scored(g, cosine(qVec, qNorm, g)))
                .sorted(Comparator.comparingDouble(Scored::sim).reversed())
                .toList();

        List<Scored> top = ranked.stream()
                .filter(s -> s.sim > 0)
                .limit(MAX_GUIDES)
                .toList();
        if (top.isEmpty()) top = List.of(ranked.get(0)); // fallback

        return top.stream()
                .map(s -> "== " + s.g.filename + " ==\n" + s.g.content)
                .collect(Collectors.joining("\n\n"));
    }

    private double cosine(Map<String, Double> qVec, double qNorm, GuideData g) {
        double dot = 0;
        for (var e : qVec.entrySet()) {
            dot += e.getValue() * g.tfidf.getOrDefault(e.getKey(), 0.0);
        }
        return (g.norm == 0) ? 0 : dot / (qNorm * g.norm);
    }

    private Map<String, Integer> tokenize(String text) {
        Map<String, Integer> tf = new HashMap<>();
        Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(t -> t.length() > MIN_TOKEN_LEN)
                .forEach(t -> tf.merge(t, 1, Integer::sum));
        return tf;
    }
}

