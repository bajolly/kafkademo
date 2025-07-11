package com.jollyworks.kafkademo.pipeline.token;

import java.util.List;

public interface TextSplitter {
    List<String> split(String text);
    String getAlgorithmName();
}