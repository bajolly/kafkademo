package com.jollyworks.kafkademo.pipeline.config;

import java.security.MessageDigest;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jollyworks.kafkademo.platform.StringHashFunction;


@Configuration
public class Udf {

    Function<byte[], String> toHexString = (input) -> {
        StringBuilder hexString = new StringBuilder();
        for (byte b : input) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    };


    @Bean
    public StringHashFunction stringHashFunction() {
        return (input) -> {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(input.getBytes("UTF-8"));
        return toHexString.apply(hash);
        };
    }
    
}
