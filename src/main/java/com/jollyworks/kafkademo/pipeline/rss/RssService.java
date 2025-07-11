package com.jollyworks.kafkademo.pipeline.rss;

import java.util.List;
import java.util.stream.Collectors;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;


import org.springframework.stereotype.Service;

import com.jollyworks.kafkademo.pipeline.content.HtmlReader;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;



@Service
public class RssService {


    HtmlReader htmlReader;
    
    public RssService(HtmlReader htmlReader) {
        this.htmlReader = htmlReader;
    }
    
    /**
     * Fetches RSS feed and extracts URLs from the entries.
     */
    public List<String> fetchRssUrls(String rssUrl) throws Exception {
        URI uri = URI.create(rssUrl); // Proper URI creation
        URL url = uri.toURL();        // Safe conversion

        try (InputStream inputStream = url.openStream(); XmlReader reader = new XmlReader(inputStream)) {
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(reader);
            return feed.getEntries().stream()
                    .map(SyndEntry::getLink)
                    .collect(Collectors.toList());
        }
    }

    public String fetchHtmlContent(String pageUrl) {
        try {
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(pageUrl).get();
            return doc.text();
        } catch (Exception e) {
            return "Failed to fetch content from " + pageUrl + ": " + e.getMessage();
        }
    }
}
