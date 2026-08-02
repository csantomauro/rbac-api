package com.cs.rbac_api.service;

import com.cs.rbac_api.dto.ArticleResponseDto;
import com.cs.rbac_api.dto.CreateArticleRequestDto;
import com.cs.rbac_api.exception.ArticleNotFoundException;
import com.cs.rbac_api.exception.UnauthorizedArticleAccessException;
import com.cs.rbac_api.model.Article;
import com.cs.rbac_api.model.Role;
import com.cs.rbac_api.model.User;
import com.cs.rbac_api.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ArticleService articleService;

    private User author;
    private User otherEditor;
    private User admin;
    private Article article;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setUsername("mario");
        author.setRole(Role.EDITOR);

        otherEditor = new User();
        otherEditor.setId(2L);
        otherEditor.setUsername("luca");
        otherEditor.setRole(Role.EDITOR);

        admin = new User();
        admin.setId(3L);
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);

        article = new Article();
        article.setId(10L);
        article.setTitle("Original title");
        article.setContent("Original content");
        article.setAuthor(author);
    }

    @Test
    void createArticle_shouldSetAuthenticatedUserAsAuthor() {
        when(authentication.getPrincipal()).thenReturn(author);
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateArticleRequestDto request = new CreateArticleRequestDto();
        request.setTitle("New article");
        request.setContent("Some content");

        ArticleResponseDto result = articleService.createArticle(request, authentication);

        assertThat(result.getAuthorUsername()).isEqualTo("mario");
        verify(articleRepository).save(any(Article.class));
    }

    @Test
    void updateArticle_shouldSucceed_whenUserIsTheAuthor() {
        when(articleRepository.findById(10L)).thenReturn(Optional.of(article));
        when(authentication.getPrincipal()).thenReturn(author);
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateArticleRequestDto request = new CreateArticleRequestDto();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        ArticleResponseDto result = articleService.updateArticle(10L, request, authentication);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void updateArticle_shouldSucceed_whenUserIsAdminButNotAuthor() {
        when(articleRepository.findById(10L)).thenReturn(Optional.of(article));
        when(authentication.getPrincipal()).thenReturn(admin);
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateArticleRequestDto request = new CreateArticleRequestDto();
        request.setTitle("Admin edit");
        request.setContent("Admin Content");

        ArticleResponseDto result = articleService.updateArticle(10L, request, authentication);

        assertThat(result.getTitle()).isEqualTo("Admin edit");
    }

    @Test
    void updateArticle_shouldThrow_whenUserIsNeitherAuthorNorAdmin() {
        when(articleRepository.findById(10L)).thenReturn(Optional.of(article));
        when(authentication.getPrincipal()).thenReturn(otherEditor);

        CreateArticleRequestDto request = new CreateArticleRequestDto();
        request.setTitle("Should not go through");
        request.setContent("Should not go through");

        assertThatThrownBy(() -> articleService.updateArticle(10L, request, authentication))
                .isInstanceOf(UnauthorizedArticleAccessException.class);

        verify(articleRepository, never()).save(any(Article.class));
    }

    @Test
    void updateArticle_shouldThrow_whenArticleDoesNotExist() {
        when(articleRepository.findById(99L)).thenReturn(Optional.empty());

        CreateArticleRequestDto request = new CreateArticleRequestDto();
        request.setTitle("Irrelevant");
        request.setContent("Irrelevant");

        assertThatThrownBy(() -> articleService.updateArticle(99L, request, authentication))
                .isInstanceOf(ArticleNotFoundException.class);
    }
}
