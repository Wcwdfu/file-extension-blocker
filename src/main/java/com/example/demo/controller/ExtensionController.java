package com.example.demo.controller;

import com.example.demo.dto.ExtensionRequest;
import com.example.demo.dto.ExtensionResponse;
import com.example.demo.service.ExtensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extensions")
@RequiredArgsConstructor
public class ExtensionController {
    private final ExtensionService service;

    @GetMapping
    public List<ExtensionResponse> getAll() {
        return service.getAll()
                .stream()
                .map(ExtensionResponse::from)
                .toList();
    }

    @PostMapping
    public void add(@Valid @RequestBody ExtensionRequest request) {
        service.addCustomExtension(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteExtension(id);
    }

    @PatchMapping("/{id}/toggle")
    public void toggle(@PathVariable Long id) {
        service.toggleExtension(id);
    }
}
