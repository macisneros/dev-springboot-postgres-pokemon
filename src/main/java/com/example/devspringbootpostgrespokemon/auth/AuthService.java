package com.example.devspringbootpostgrespokemon.auth;

import com.example.devspringbootpostgrespokemon.auth.dto.LoginRequest;
import com.example.devspringbootpostgrespokemon.auth.dto.TokenResponse;
import com.example.devspringbootpostgrespokemon.security.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * Valida las credenciales y emite un token de acceso.
     *
     * @throws org.springframework.security.core.AuthenticationException si las credenciales no son válidas
     */
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        return jwtTokenService.generateToken(authentication);
    }
}
