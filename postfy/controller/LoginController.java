package com.auer.postfy.controller;

import com.auer.postfy.dto.ErrorResponseDTO;
import com.auer.postfy.dto.LoginRequestDTO;
import com.auer.postfy.entity.User;
import com.auer.postfy.repository.UserRepository;
import com.auer.postfy.security.JwtService;
import com.auer.postfy.security.TokenWhiteList;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginController(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        // Busca o usuário pelo email
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

        // Se usuário não existe ou senha está incorreta, retorna 401 com mensagem genérica
        if (user == null || !passwordEncoder.matches(dto.getSenha(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Credenciais Incorretas"));
        }

        // cria Authentication mínimo só com o username
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(
                        new SimpleGrantedAuthority("READ"),
                        new SimpleGrantedAuthority("WRITE"),
                        new SimpleGrantedAuthority("DELETE"),
                        new SimpleGrantedAuthority("ADMIN")
                )
        );

        String token = jwtService.generateToken(auth, user.getId());
        TokenWhiteList.addToken(token);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // tratamento de validação 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        var detalhes = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO("Requisição inválida", detalhes));
    }

    // 404 – usuário não encontrado
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensagem", "Usuário não encontrado"));
    }

    // 500 – erro global
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Erro interno do servidor",
                        List.of(ex.getMessage())));
    }

    // exceção custom para 404
    static class UserNotFoundException extends RuntimeException {}
}
