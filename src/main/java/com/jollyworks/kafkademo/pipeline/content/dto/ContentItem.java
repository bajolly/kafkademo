package com.jollyworks.kafkademo.pipeline.content.dto;

import java.time.LocalDateTime;

public record ContentItem(
    String id,
    String title,
    String url,
    String content,
    String pubDate,
    LocalDateTime processedAt
) {
}