package com.lrs.SSB.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "insurances")
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "coverage", nullable = false)
    private String coverage;

    @Column(name = "benefits", nullable = false)
    private String benefits;

    @Column(name = "provider", nullable = false)
    private String provider;

    public Insurance() {}

    public Insurance(String title, String category, String price, String coverage, String benefits, String provider) {
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
