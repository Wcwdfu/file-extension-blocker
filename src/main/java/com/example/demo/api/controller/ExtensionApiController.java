package com.example.demo.controller;

import com.example.demo.dto.ExtensionRequest;
import com.example.demo.dto.ExtensionResponse;
import com.example.demo.service.ExtensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionApiController {
    private final ExtensionService service;

    @GetMapping
    public List<ExtensionResponse> getAll() {
        return service.getAll().stream().map(ExtensionResponse::from).toList();
    }

    @GetMapping("/fixed")
    public List<ExtensionResponse> getFixed() {
        return service.getFixed().stream().map(ExtensionResponse::from).toList();
    }

    @GetMapping("/custom")
    public List<ExtensionResponse> getCustom() {
        return service.getCustom().stream().map(ExtensionResponse::from).toList();
    }

    @GetMapping("/custom/count")
    public long getCustomCount() {
        return service.getCustomCount();
    }

    @PostMapping("/custom")
    public ExtensionResponse addCustom(@Valid @RequestBody ExtensionRequest request) {
        return ExtensionResponse.from(service.addCustomExtension(request));
    }

    @DeleteMapping("/custom/{id}")
    public void deleteCustom(@PathVariable Long id) {
        service.deleteCustom(id);
    }

    @PatchMapping("/{id}/toggle")
    public ExtensionResponse toggle(@PathVariable Long id) {
        return ExtensionResponse.from(service.toggle(id));
    }
}
