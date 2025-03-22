package com.lrs.SSB.controller;

public class UtilityDTO {
    private Long id;
    private String serviceName;
    private String iban;
    private String logoUrl;
    private String category;
    private Long destinationCardId;

    public UtilityDTO() {}

    public UtilityDTO(
            Long id,
            String serviceName,
            String iban,
            String logoUrl,
            String category,
            Long destinationCardId
    ) {
        this.id = id;
        this.serviceName = serviceName;
        this.iban = iban;
        this.logoUrl = logoUrl;
        this.category = category;
        this.destinationCardId = destinationCardId;
    }


    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getIban() {
        return iban;
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
