package com.example.demo.service;

import com.example.demo.domain.BlockedExtension;
import com.example.demo.domain.ExtensionType;
import com.example.demo.dto.ExtensionRequest;
import com.example.demo.repository.BlockedExtensionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtensionService {
    private static final int CUSTOM_LIMIT = 200;

    private final BlockedExtensionRepository repo;

    public List<BlockedExtension> getAll() {
        return repo.findAll();
    }

    public void addCustomExtension(ExtensionRequest request) {

        String ext = normalize(request.extension());

        if (repo.findByExtension(ext).isPresent()) {
            throw new RuntimeException("이미 존재하는 확장자입니다.");
        }

        long count = repo.countByType(ExtensionType.CUSTOM);
        if (count >= CUSTOM_LIMIT) {
            throw new RuntimeException("추가 확장자는 최대 200개까지 가능합니다.");
        }

        BlockedExtension entity = new BlockedExtension(ext, true, ExtensionType.CUSTOM);

        repo.save(entity);
    }

    public void deleteExtension(Long id) {
        BlockedExtension entity = repo.findById(id).orElseThrow(()-> new RuntimeException("존재하지 않은 확장자 입니다."));
        repo.delete(entity);
    }

    @Transactional
    public void toggleExtension(Long id) {
        BlockedExtension extension = repo.findById(id).orElseThrow(() -> new RuntimeException("등록되지 않은 확장자 입니다."));
        extension.toggleBlocked();
    }

    private String normalize(String extension) {
        return extension.trim().toLowerCase();
    }
}
