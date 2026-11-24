package com.example.devspringbootpostgrespokemon.auth.dto;

public class TokenResponse {

    private static final String BEARER = "Bearer";

    private final String accessToken;
    private final String tokenType;
    private final long expiresIn;

    private TokenResponse(String accessToken, String tokenType, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public static TokenResponse bearer(String accessToken, long expiresInSeconds) {
        return new TokenResponse(accessToken, BEARER, expiresInSeconds);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
