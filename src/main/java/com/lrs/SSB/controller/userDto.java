package com.lrs.SSB.controller;

public class userDto {
    private String numeComplet;
    private String email;
    private String telefon;
    private String parola;
    private String seriaId;
    private String numarId;
    private String dataNasterii;
    private String judet;
    private String localitate;
    private String blockchainPrivateKey;
    private boolean savingsActive;
    private int roundingMultiple;
    public String getNumeComplet() {
        return numeComplet;
    }

    public void setNumeComplet(String numeComplet) {
        this.numeComplet = numeComplet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public String getSeriaId() {
        return seriaId;
    }

    public void setSeriaId(String seriaId) {
        this.seriaId = seriaId;
    }

    public String getNumarId() {
        return numarId;
    }

    public void setNumarId(String numarId) {
        this.numarId = numarId;
    }

    public String getDataNasterii() {
        return dataNasterii;
    }

    public void setDataNasterii(String dataNasterii) {
        this.dataNasterii = dataNasterii;
    }

    public String getJudet() {
        return judet;
    }

    public void setJudet(String judet) {
        this.judet = judet;
    }

    public String getLocalitate() {
        return localitate;
    }

    public void setLocalitate(String localitate) {
        this.localitate = localitate;
    }

    public String getBlockchainPrivateKey() {
        return blockchainPrivateKey;
    }

    public void setBlockchainPrivateKey(String blockchainPrivateKey) {
        this.blockchainPrivateKey = blockchainPrivateKey;
    }

    public boolean isSavingsActive() {
        return savingsActive;
    }

    public void setSavingsActive(boolean savingsActive) {
        this.savingsActive = savingsActive;
    }

    public int getRoundingMultiple() {
        return roundingMultiple;
    }

    public void setRoundingMultiple(int roundingMultiple) {
        this.roundingMultiple = roundingMultiple;
    }
}
