package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public class UsuarioAutenticado {

    private UsuarioAutenticado() {

    }
    public static UUID id (Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
