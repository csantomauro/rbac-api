package com.cs.rbac_api.controller;

import com.cs.rbac_api.dto.ArticleResponseDto;
import com.cs.rbac_api.dto.CreateArticleRequestDto;
import com.cs.rbac_api.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleResponseDto>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public ResponseEntity<ArticleResponseDto> createArticle(
            @Valid @RequestBody CreateArticleRequestDto request,
            Authentication authentication) {
        ArticleResponseDto created = articleService.createArticle(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EDITOR', 'ADMIN')")
    public ResponseEntity<ArticleResponseDto> updatArticle(
            @PathVariable Long id,
            @Valid @RequestBody CreateArticleRequestDto request,
            Authentication authentication) {
        return ResponseEntity.ok(articleService.updateArticle(id, request, authentication));
    }
}
