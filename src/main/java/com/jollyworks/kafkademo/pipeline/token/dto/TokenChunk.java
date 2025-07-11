package com.jollyworks.kafkademo.pipeline.token.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record TokenChunk(
    String id,
    String contentId,
    String chunkText,
    int chunkIndex,
    String splitterAlgorithm,
    Map<String, Object> metadata,
    LocalDateTime processedAt
) {
}