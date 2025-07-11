package com.jollyworks.kafkademo.platform;


import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Gate {
        private static final Map<String, Sinks.One<Void>> gates = new ConcurrentHashMap<>();

    public static Mono<Void> waitFor(String gateName) {
        return gates.computeIfAbsent(gateName, k -> Sinks.one()).asMono();
    }

    public static void open(String gateName) {
        gates.computeIfAbsent(gateName, k -> Sinks.one()).tryEmitEmpty();
    }
}
