package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ExtensionRequest(
        @NotBlank(message = "확장자를 입력해주세요.")
        @Size(max = 20, message = "확장자는 20자 이하여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z]+$", message = "확장자는 영어만 입력 가능합니다.")
        String extension
) {}
