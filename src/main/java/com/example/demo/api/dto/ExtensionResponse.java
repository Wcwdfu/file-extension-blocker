package com.example.demo.api.dto;

import com.example.demo.domain.BlockedExtension;

public record ExtensionResponse(
        Long id,
        String extension,
        boolean blocked,
        String type
) {
    public static ExtensionResponse from(BlockedExtension entity) {
        return new ExtensionResponse(
                entity.getId(),
                entity.getExtension(),
                entity.isBlocked(),
                entity.getType().name()
        );
    }
}
