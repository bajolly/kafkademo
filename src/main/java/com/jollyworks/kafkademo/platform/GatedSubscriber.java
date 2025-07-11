package com.jollyworks.kafkademo.platform;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;

public class GatedSubscriber<T> extends BaseSubscriber<T> {
    private final String gateName;

    public GatedSubscriber(String gateName) {
        this.gateName = gateName;
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        // Wait for the gate to open before requesting data
        Gate.waitFor(gateName).doOnTerminate(() -> request(Long.MAX_VALUE)).subscribe();
    }

    @Override
    protected void hookOnNext(T value) {
        // Process the value as needed
        System.out.println("Received: " + value);
    }

    @Override
    protected void hookFinally(SignalType type) {
        System.out.println("Stream finished with signal: " + type);
    }
}
