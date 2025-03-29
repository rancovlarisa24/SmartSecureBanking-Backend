package com.lrs.SSB.controller;

import com.lrs.SSB.controller.ArticleDTO;
import com.lrs.SSB.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> saveArticle(@RequestBody ArticleDTO articleDTO) {
        ArticleDTO savedArticle = articleService.saveArticle(articleDTO);
        return ResponseEntity.ok(savedArticle);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ArticleDTO>> getArticlesByUser(@PathVariable Integer userId) {
        List<ArticleDTO> articles = articleService.getArticlesByUserId(userId);
        return ResponseEntity.ok(articles);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteArticle(
            @RequestParam Integer userId,
            @RequestParam String articleUrl) {
        articleService.deleteArticle(userId, articleUrl);
        return ResponseEntity.ok().build();
    }

}
