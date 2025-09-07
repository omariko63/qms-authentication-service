package com.qualitymanagementsystem.auth_service.util;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTokenStore {
    private static final ConcurrentHashMap<String, Long> tokens = new ConcurrentHashMap<>();

    //storeToken
    public static void storeToken(String token) {
        tokens.put(token, System.currentTimeMillis());
    }

    //updateActivity
    public static void updateActivity(String token) {
        if (tokens.containsKey(token)) {
            tokens.put(token, System.currentTimeMillis());
        }
    }

    // check the token is valid
    public static boolean isTokenValid(String token) {
        return tokens.containsKey(token);
    }

    //to remove token
    public static void invalidateToken(String token) {
        tokens.remove(token);
    }

    public static ConcurrentHashMap<String, Long> getTokens() {
        return tokens;
    }
}
