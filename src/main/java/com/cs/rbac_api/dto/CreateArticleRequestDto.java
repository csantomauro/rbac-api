package com.cs.rbac_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArticleRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message =  "Content is required")
    private String content;
}
