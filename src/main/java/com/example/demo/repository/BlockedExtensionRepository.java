package com.example.demo.repository;

import com.example.demo.domain.BlockedExtension;
import com.example.demo.domain.ExtensionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockedExtensionRepository extends JpaRepository<BlockedExtension, Long> {
    Optional<BlockedExtension> findByExtension(String extension);
    long countByType(ExtensionType type);
    List<BlockedExtension> findAllByTypeOrderByExtensionAsc(ExtensionType type);

    void deleteAllByType(ExtensionType type);
    List<BlockedExtension> findAllByType(ExtensionType type);
}
