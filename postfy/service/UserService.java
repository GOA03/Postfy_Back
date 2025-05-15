package com.auer.postfy.service;

import com.auer.postfy.dto.UserRequestDTO;
import com.auer.postfy.entity.User;
import com.auer.postfy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void cadastrarUsuario(UserRequestDTO dto) {
        if (repository.findByUsername(dto.getNome()).isPresent()) {
            throw new RuntimeException("Nome de usuário já existe");
        }

        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User();
        user.setUsername(dto.getNome());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getSenha()));
        user.setCreatedAt(LocalDateTime.now());

        repository.save(user);
    }

    @Transactional
    public User atualizarUsuario(String email, String nome, String senha) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (nome != null && !nome.isBlank()) {
            if (nome.length() > 255) {
                throw new RuntimeException("Nome deve ter no máximo 255 caracteres.");
            }
            user.setUsername(nome);
        }

        if (senha != null && !senha.isBlank()) {
            if (senha.length() < 8 || senha.length() > 20) {
                throw new RuntimeException("Senha deve ter entre 8 e 20 caracteres.");
            }
            user.setPassword(passwordEncoder.encode(senha)); // só codifica senha válida
        }

        return repository.save(user);
    }


    public void excluirUsuario(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        repository.delete(user);
    }

    public User buscarPorUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public User buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
