package com.example.devspringbootpostgrespokemon.security;

import com.example.devspringbootpostgrespokemon.auth.dto.TokenResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/** Emite tokens JWT firmados para usuarios ya autenticados. */
@Service
public class JwtTokenService {

    static final String ROLES_CLAIM = "roles";
    static final String ROLE_PREFIX = "ROLE_";

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration expiration;

    public JwtTokenService(JwtEncoder jwtEncoder, AppSecurityProperties properties) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = properties.getJwt().getIssuer();
        this.expiration = properties.getJwt().getExpiration();
    }

    public TokenResponse generateToken(Authentication authentication) {
        Instant issuedAt = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(authentication.getName())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plus(expiration))
                .claim(ROLES_CLAIM, extractRoles(authentication))
                .build();

        JwsHeader header = JwsHeader.with(JwtConfig.SIGNING_ALGORITHM).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return TokenResponse.bearer(token, expiration.toSeconds());
    }

    private static List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .collect(Collectors.toList());
    }
}
