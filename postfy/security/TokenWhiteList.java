package com.auer.postfy.security;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TokenWhiteList {
    private static final Set<String> validTokens = ConcurrentHashMap.newKeySet();

    public static void addToken(String token) {
        validTokens.add(token);
    }

    public static void removeToken(String token) {
        validTokens.remove(token);
    }

    public static boolean isTokenValid(String token) {
        return validTokens.contains(token);
    }
}
