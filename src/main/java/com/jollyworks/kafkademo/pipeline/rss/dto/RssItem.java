package com.jollyworks.kafkademo.pipeline.rss.dto;

public record RssItem(String id, String title, String description, String url, String guid, String pubDate) {
}
