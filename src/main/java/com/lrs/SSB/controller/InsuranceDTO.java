package com.lrs.SSB.controller;

public class InsuranceDTO {
    private Long id;
    private String title;
    private String category;
    private String price;
    private String coverage;
    private String benefits;
    private String provider;

    public InsuranceDTO() {}

    public InsuranceDTO(Long id, String title, String category, String price, String coverage, String benefits, String provider) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
        this.coverage = coverage;
        this.benefits = benefits;
        this.provider = provider;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getCoverage() { return coverage; }
    public void setCoverage(String coverage) { this.coverage = coverage; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}
