package com.cs.rbac_api.repository;

import com.cs.rbac_api.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
