package com.lrs.SSB.service;

import com.lrs.SSB.controller.ArticleDTO;
import com.lrs.SSB.entity.Article;
import com.lrs.SSB.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository){
        this.articleRepository = articleRepository;
    }

    public ArticleDTO saveArticle(ArticleDTO articleDTO) {
        Article article = mapToEntity(articleDTO);
        Article savedArticle = articleRepository.save(article);
        return mapToDTO(savedArticle);
    }

    public List<ArticleDTO> getArticlesByUserId(Integer userId) {
        List<Article> articles = articleRepository.findByUserId(userId);
        return articles.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteArticle(Integer userId, String articleUrl) {
        articleRepository.deleteByUserIdAndArticleUrl(userId, articleUrl);
    }

    private ArticleDTO mapToDTO(Article article) {
        return new ArticleDTO(
                article.getId(),
                article.getUserId(),
                article.getArticleUrl(),
                article.getTitle(),
                article.getDescription(),
                article.getFullArticle(),
                article.getSavedAt()
        );
    }

    private Article mapToEntity(ArticleDTO articleDTO) {
        Article article = new Article();
        article.setUserId(articleDTO.getUserId());
        article.setArticleUrl(articleDTO.getArticleUrl());
        article.setTitle(articleDTO.getTitle());
        article.setDescription(articleDTO.getDescription());
        article.setFullArticle(articleDTO.getFullArticle());
        return article;
    }
}
