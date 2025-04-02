package com.jollyworks.kafkademo.messages;


import lombok.Data;


@Data
public class RssItem {

    private String description;
    private String title;
    private String url;
    private String guid;
    private String pubDate;

    public RssItem(String id, String title, String description, String url, String guid, String pubDate) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.guid = guid;
        this.pubDate = pubDate;
    }
}
