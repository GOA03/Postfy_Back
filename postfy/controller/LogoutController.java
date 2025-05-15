package com.auer.postfy.controller;

import com.auer.postfy.security.TokenWhiteList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LogoutController {

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader,
                                    Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("mensagem", "Usuario não autorizado"));
            }

            // Verifica e extrai o token do cabeçalho
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("mensagem", "Erro na requisição", "erro", "Authorization inválido ou ausente"));
            }

            String token = authorizationHeader.substring(7); // remove "Bearer "

            // Remove da whitelist
            TokenWhiteList.removeToken(token);

            // Limpa o contexto de segurança
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok(Map.of("mensagem", "Logout realizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensagem", "Erro interno do servidor", "erro", e.getMessage()));
        }
    }
}
