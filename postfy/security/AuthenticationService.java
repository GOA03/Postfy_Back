package com.auer.postfy.security;

import com.auer.postfy.entity.User;
import com.auer.postfy.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthenticationService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public String authenticate(Authentication authentication) {
        // Obtem o nome de usuário a partir da autenticação
        String username = authentication.getName();

        // Busca o usuário no repositório
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Obtem o ID do usuário
        Long userId = user.getId();

        // Gera o token passando o ID do usuário
        return jwtService.generateToken(authentication, userId);
    }
}