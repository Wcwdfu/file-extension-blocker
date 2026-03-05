package com.example.demo.api.dto;

import java.util.List;

// 전체 페이지 렌더링에 필요한 고정, 커스텀 확장자 와 커스텀 확장자 수
public record ExtensionPageResponse(
        List<ExtensionResponse> fixed,
        List<ExtensionResponse> custom,
        long customCount
) {
}
