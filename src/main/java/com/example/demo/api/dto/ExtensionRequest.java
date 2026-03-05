package com.example.demo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ExtensionRequest(
        @NotBlank(message = "확장자를 입력해주세요.")
        @Size(max = 20, message = "확장자는 20자 이하여야 합니다.")
        @Pattern(regexp = "^[a-z0-9]+(\\.[a-z0-9]+)*$", message = "올바른 형태의 확장자를 입력해주세요.")
        String extension
) {}
