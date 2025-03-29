package com.lrs.SSB.controller;

import java.time.LocalDateTime;

public class ArticleDTO {

    private Long id;
    private Integer userId;
    private String articleUrl;
    private String title;
    private String description;
    private String fullArticle;
    private LocalDateTime savedAt;

    public ArticleDTO() {}

    public ArticleDTO(Long id, Integer userId, String articleUrl, String title, String description, String fullArticle, LocalDateTime savedAt) {
        this.id = id;
        this.userId = userId;
        this.articleUrl = articleUrl;
        this.title = title;
        this.description = description;
        this.fullArticle = fullArticle;
        this.savedAt = savedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullArticle() {
        return fullArticle;
    }

    public void setFullArticle(String fullArticle) {
        this.fullArticle = fullArticle;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
