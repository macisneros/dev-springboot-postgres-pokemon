package com.example.devspringbootpostgrespokemon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PokemonControllerSecurityTest {

    private static final String POKEMON_URL = "/pokemon/all";
    private static final String ISSUER = "test-issuer";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void requestWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get(POKEMON_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE));
    }

    @Test
    void userRoleCanListPokemon() throws Exception {
        String token = obtainToken("ash", "ash-test-password");

        mockMvc.perform(get(POKEMON_URL).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void adminRoleCanListPokemon() throws Exception {
        String token = obtainToken("admin", "admin-test-password");

        mockMvc.perform(get(POKEMON_URL).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk());
    }

    @Test
    void tokenWithoutRequiredRoleIsForbidden() throws Exception {
        String token = encode(Instant.now().plus(5, ChronoUnit.MINUTES), List.of("GUEST"), ISSUER);

        mockMvc.perform(get(POKEMON_URL).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isForbidden());
    }

    @Test
    void expiredTokenIsUnauthorized() throws Exception {
        String token = encode(Instant.now().minus(5, ChronoUnit.MINUTES), List.of("USER"), ISSUER);

        mockMvc.perform(get(POKEMON_URL).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenFromAnotherIssuerIsUnauthorized() throws Exception {
        String token = encode(Instant.now().plus(5, ChronoUnit.MINUTES), List.of("USER"), "evil-issuer");

        mockMvc.perform(get(POKEMON_URL).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenSignedWithAnotherKeyIsUnauthorized() throws Exception {
        byte[] foreignSecret = "another-secret-key-with-at-least-32-chars".getBytes(StandardCharsets.UTF_8);
        JwtEncoder foreignEncoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(new SecretKeySpec(foreignSecret, "HmacSHA256")));
        String token = encode(foreignEncoder, Instant.now().plus(5, ChronoUnit.MINUTES), List.of("USER"), ISSUER);

        mockMvc.perform(get(POKEMON_URL).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isUnauthorized());
    }

    private String obtainToken(String username, String password) throws Exception {
        String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private String encode(Instant expiresAt, List<String> roles, String issuer) {
        return encode(jwtEncoder, expiresAt, roles, issuer);
    }

    private static String encode(JwtEncoder encoder, Instant expiresAt, List<String> roles, String issuer) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("test-user")
                .issuedAt(expiresAt.minus(1, ChronoUnit.HOURS))
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }
}
