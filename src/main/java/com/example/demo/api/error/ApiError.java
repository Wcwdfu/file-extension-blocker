package com.example.demo.api.error;

import java.time.Instant;

public record ApiError(
        int status,
        String message,
        String path,
        Instant timeStamp
) {
}
