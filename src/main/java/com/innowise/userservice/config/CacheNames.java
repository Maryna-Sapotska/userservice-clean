package com.innowise.userservice.config;

public final class CacheNames {

    private CacheNames() {
        throw new IllegalStateException("Utility class");
    }

    public static final String USERS = "users";
    public static final String USERS_WITH_CARDS = "users_with_cards";
    public static final String CARDS = "cards";
}
