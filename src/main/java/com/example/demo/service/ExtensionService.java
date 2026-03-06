package com.example.demo.service;

import com.example.demo.api.error.ApiException;
import com.example.demo.domain.BlockedExtension;
import com.example.demo.domain.ExtensionType;
import com.example.demo.api.dto.ExtensionRequest;
import com.example.demo.repository.BlockedExtensionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtensionService {
    private static final int CUSTOM_LIMIT = 200;
    private final BlockedExtensionRepository repo;

    public List<BlockedExtension> getFixed() {
        return repo.findAllByTypeOrderByExtensionAsc(ExtensionType.FIXED);
    }

    public List<BlockedExtension> getCustom() {
        return repo.findAllByTypeOrderByExtensionAsc(ExtensionType.CUSTOM);
    }

    @Transactional
    public BlockedExtension addCustomExtension(ExtensionRequest request) {

        String ext = normalize(request.extension());

        long count = repo.countByType(ExtensionType.CUSTOM);
        if (count >= CUSTOM_LIMIT) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "추가 확장자는 최대 200개까지 가능합니다.");
        }

        if (repo.findByTypeAndExtension(ExtensionType.CUSTOM, ext).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 존재하는 확장자입니다.");
        }

        BlockedExtension saved = repo.save(new BlockedExtension(ext, true, ExtensionType.CUSTOM));
        return saved;
    }

    @Transactional
    public void deleteCustom(Long id) {

        BlockedExtension e = repo.findById(id).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "등록되지 않은 확장자입니다.")
        );

        if (e.getType() != ExtensionType.CUSTOM) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "고정 확장자는 삭제할 수 없습니다.");
        }

        repo.delete(e);
    }

    @Transactional
    public BlockedExtension updateBlocked(Long id, Boolean blocked) {
        BlockedExtension e = repo.findById(id).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND , "등록되지 않은 확장자입니다.")
        );
        if (blocked == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "blocked 값이 필요합니다.");
        }
        if (e.getType() == ExtensionType.CUSTOM) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "커스텀 확장자는 토글이 불가능합니다. x버튼으로 삭제해 주세요.");
        }
        e.updateBlocked(blocked);
        return e;
    }

    @Transactional
    public void resetAll() {
        repo.deleteAllByType(ExtensionType.CUSTOM);

        List<BlockedExtension> fixed = repo.findAllByType(ExtensionType.FIXED);
        for (BlockedExtension e : fixed) {
            e.updateBlocked(false);
        }
    }

    private String normalize(String extension) {
        return extension.trim().toLowerCase();
    }
}
