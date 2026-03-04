package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "blocked_extension")
@Getter
@NoArgsConstructor
public class BlockedExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String extension;

    private boolean blocked;

    @Enumerated(EnumType.STRING)
    private ExtensionType type;

    public void toggleBlocked() {
        this.blocked = !this.blocked;
    }

    public BlockedExtension(String extension, boolean blocked, ExtensionType type) {
        this.extension = extension;
        this.blocked = blocked;
        this.type = type;
    }
}

