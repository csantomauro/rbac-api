package com.cs.rbac_api.service;

import com.cs.rbac_api.dto.ArticleResponseDto;
import com.cs.rbac_api.dto.CreateArticleRequestDto;
import com.cs.rbac_api.exception.ArticleNotFoundException;
import com.cs.rbac_api.exception.UnauthorizedArticleAccessException;
import com.cs.rbac_api.model.Article;
import com.cs.rbac_api.model.User;
import com.cs.rbac_api.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleResponseDto createArticle(CreateArticleRequestDto request, Authentication authentication) {
        User author = (User) authentication.getPrincipal();

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setAuthor(author);

        Article saved = articleRepository.save(article);
        return toResponseDto(saved);
    }

    public List<ArticleResponseDto> getAllArticles() {
        return articleRepository.findAll().stream().map(this::toResponseDto).toList();
    }

    public ArticleResponseDto updateArticle(Long id, CreateArticleRequestDto request, Authentication authentication) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found: " + id));

        User currentUser = (User) authentication.getPrincipal();
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        boolean isAuthor = article.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isAuthor) {
            throw new UnauthorizedArticleAccessException("You can only edit your own articles");
        }

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        return toResponseDto(articleRepository.save(article));
    }

    private ArticleResponseDto toResponseDto(Article article) {
        return new ArticleResponseDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getAuthor().getUsername(),
                article.getCreatedAt()
        );
    }
}
