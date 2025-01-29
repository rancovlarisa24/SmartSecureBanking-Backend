package com.lrs.SSB.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numeComplet;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String telefon;

    @Column(nullable = false)
    private String parola;

    @Column(nullable = false)
    private Boolean activated;

    @Column
    private String codActivare;

    @Column
    private String seriaId;

    @Column
    private String numarId;

    @Column
    private String dataNasterii;

    @Column(nullable = false)
    private String judet;

    @Column(nullable = false)
    private String localitate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getCodActivare() {
        return codActivare;
    }

    public void setCodActivare(String codActivare) {
        this.codActivare = codActivare;
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
}
