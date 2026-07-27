package com.cs.rbac_api.repository;

import com.cs.rbac_api.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a JOIN FETCH a.author")
    List<Article> findAllWithAuthor();
}
