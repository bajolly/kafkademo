package com.jollyworks.kafkademo.platform;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


@FunctionalInterface
public interface StringHashFunction {
    String apply(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException;
}
