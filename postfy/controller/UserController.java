package com.auer.postfy.controller;

import com.auer.postfy.dto.*;
import com.auer.postfy.entity.User;
import com.auer.postfy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody UsuarioWrapperDTO wrapper) {
        try {
            UserRequestDTO dto = wrapper.getUsuario();
            service.cadastrarUsuario(dto);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Sucesso ao cadastrar usuario");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("Erro ao cadastrar usuario", List.of(e.getMessage())));
        }
    }


    @GetMapping
    public ResponseEntity<?> buscarDados(Authentication authentication) {
        try {
            // Obtém o nome de usuário a partir da autenticação
            String username = authentication.getName();

            // Busca o usuário pelo email
            User user = service.buscarPorEmail(username);

            // Cria o objeto de resposta com a estrutura correta
            UserResponseDTO data = new UserResponseDTO(user.getUsername(), user.getEmail());

            Map<String, Object> resp = new HashMap<>();
            resp.put("mensagem", "Sucesso ao buscar usuario");
            resp.put("usuario", data); // Ajuste aqui para "usuario"

            return ResponseEntity.ok(resp);

        } catch (RuntimeException e) {
            // 400 para erros de negócio/leitura
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("Erro na requisição", List.of(e.getMessage()))); // Mensagem de erro ajustada
        }
    }

    @PutMapping
    public ResponseEntity<?> atualizarUsuario(@Valid @RequestBody UserUpdateRequestDTO dto, Authentication authentication) {
        try {
            String email = authentication.getName();
            service.atualizarUsuario(email, dto.getNome(), dto.getSenha());

            // Se a atualização for bem-sucedida
            Map<String, Object> resp = new HashMap<>();
            resp.put("mensagem", "Sucesso ao salvar o usuario");
            resp.put("usuario", new UserResponseDTO(dto.getNome(), authentication.getName())); // email vem do token
            return ResponseEntity.ok(resp);

        } catch (RuntimeException e) {
            // Erro de negócio (ex. Usuário não encontrado ou dados inválidos)
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("Erro ao salvar usuario", List.of(e.getMessage())));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> excluirUsuario(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = service.buscarPorEmail(username);

            // Excluir o usuário do serviço
            service.excluirUsuario(user.getEmail());

            Map<String, Object> resp = new HashMap<>();
            resp.put("mensagem", "Sucesso ao excluir o usuario");
            return ResponseEntity.ok(resp);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseDTO("Erro ao excluir", List.of(e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Erro interno do servidor", List.of(e.getMessage())));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        var erros = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(new ErrorResponseDTO("Erro ao cadastrar usuario", erros));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Erro interno do servidor", List.of(ex.getMessage())));
    }
}
