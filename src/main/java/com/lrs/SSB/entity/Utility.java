package com.lrs.SSB.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class Utility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(nullable = false, length = 34)
    private String iban;

    @Column(name = "private_key", nullable = false, length = 255)
    private String privateKey;

    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    @Column(length = 100)
    private String category;

    @Column(name = "destination_card_id")
    private Long destinationCardId;



    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getIban() {
        return iban;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getCategory() {
        return category;
    }

    public Long getDestinationCardId() {
        return destinationCardId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDestinationCardId(Long destinationCardId) {
        this.destinationCardId = destinationCardId;
    }


}
