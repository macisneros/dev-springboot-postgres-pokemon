package com.example.devspringbootpostgrespokemon.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.List;

/**
 * Configuración de seguridad de la aplicación, enlazada desde {@code app.security.*}.
 * Los valores sensibles (secreto JWT y contraseñas) deben llegar por variables de entorno.
 */
@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    @Valid
    @NotNull
    private final Jwt jwt;

    @Valid
    @NotEmpty
    private final List<UserAccount> users;

    public AppSecurityProperties(Jwt jwt, List<UserAccount> users) {
        this.jwt = jwt;
        this.users = users;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public List<UserAccount> getUsers() {
        return users;
    }

    public static class Jwt {

        /** Secreto HMAC-SHA256; mínimo 32 caracteres (256 bits). */
        @NotBlank
        @Size(min = 32)
        private final String secret;

        @NotBlank
        private final String issuer;

        @NotNull
        private final Duration expiration;

        public Jwt(String secret, String issuer, Duration expiration) {
            this.secret = secret;
            this.issuer = issuer;
            this.expiration = expiration;
        }

        public String getSecret() {
            return secret;
        }

        public String getIssuer() {
            return issuer;
        }

        public Duration getExpiration() {
            return expiration;
        }
    }

    public static class UserAccount {

        @NotBlank
        private final String username;

        @NotBlank
        private final String password;

        @NotEmpty
        private final List<Role> roles;

        public UserAccount(String username, String password, List<Role> roles) {
            this.username = username;
            this.password = password;
            this.roles = roles;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public List<Role> getRoles() {
            return roles;
        }
    }
}
