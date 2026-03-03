CREATE TABLE blocked_extension (
    id BIGINT NOT NULL AUTO_INCREMENT,
    extension VARCHAR(20) NOT NULL,
    type VARCHAR(10) NOT NULL,
    blocked TINYINT(1) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_type_extension (type, extension),
    CONSTRAINT chk_type CHECK (type IN ('FIXED', 'CUSTOM'))
);
