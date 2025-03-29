package com.lrs.SSB.repository;

import com.lrs.SSB.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByUserId(Integer userId);

    @Modifying
    @Transactional
    void deleteByUserIdAndArticleUrl(Integer userId, String articleUrl);
}
