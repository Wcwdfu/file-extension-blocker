package com.example.demo.api.controller;

import com.example.demo.api.dto.ExtensionPageResponse;
import com.example.demo.api.dto.ExtensionRequest;
import com.example.demo.api.dto.ExtensionResponse;
import com.example.demo.api.dto.ExtensionUpdateRequest;
import com.example.demo.service.ExtensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionApiController {
    private final ExtensionService service;

    // 1. 페이지 전체 조회
    @GetMapping
    public ExtensionPageResponse getAll() {
        List<ExtensionResponse> fixed = service.getFixed().stream().map(ExtensionResponse::from).toList();
        List<ExtensionResponse> custom = service.getCustom().stream().map(ExtensionResponse::from).toList();
        long customCount = custom.size();

        return new ExtensionPageResponse(fixed, custom, customCount);
    }

    // 2. 커스텀 확장자 추가
    @PostMapping("/custom")
    public ExtensionResponse addCustom(@Valid @RequestBody ExtensionRequest request) {
        return ExtensionResponse.from(service.addCustomExtension(request));
    }

    // 3. 커스텀 확장자 제거
    @DeleteMapping("/custom/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustom(@PathVariable Long id) {
        service.deleteCustom(id);
    }

    // 4. 고정 확장자 토글
    @PatchMapping("/{id}")
    public ExtensionResponse update(@PathVariable Long id,
                                    @RequestBody ExtensionUpdateRequest req) {
        return ExtensionResponse.from(service.updateBlocked(id, req.blocked()));
    }

    // 5. 전체 초기화
    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset() {
        service.resetAll();
    }
}
