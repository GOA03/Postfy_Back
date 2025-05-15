package com.auer.postfy.controller;

import com.auer.postfy.dto.ErrorResponseDTO;
import com.auer.postfy.dto.RascunhoRequestDTO;
import com.auer.postfy.entity.Email;
import com.auer.postfy.entity.User;
import com.auer.postfy.repository.UserRepository;
import com.auer.postfy.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rascunhos")
public class RascunhoController {

    private final EmailService emailService;
    private final UserRepository userRepository;

    public RascunhoController(EmailService emailService, UserRepository userRepository) {
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> criarRascunho(@Valid @RequestBody RascunhoRequestDTO dto, Authentication authentication) {
        // Verifica se pelo menos um campo está preenchido
        if (dto.getAssunto() == null && dto.getDestinatario() == null && dto.getCorpo() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    "Erro ao criar rascunho",
                    List.of("Pelo menos um campo deve estar preenchido.")
            ));
        }

        try {
            Email rascunho = emailService.criarRascunho(dto, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensagem", "Rascunho criado",
                    "rascunho", Map.of(
                            "emailId", rascunho.getId(),
                            "assunto", rascunho.getSubject() != null ? rascunho.getSubject() : "",
                            "destinatario", dto.getDestinatario() != null ? dto.getDestinatario() : "",
                            "corpo", rascunho.getBody() != null ? rascunho.getBody() : ""
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    "Erro ao criar rascunho",
                    List.of(e.getMessage())
            ));
        }
    }

    /*@PutMapping
    public ResponseEntity<?> atualizarRascunho(@Valid @RequestBody RascunhoRequestDTO dto, Authentication authentication) {
        // Verifica se pelo menos um campo está preenchido
        if (dto.getAssunto() == null && dto.getDestinatario() == null && dto.getCorpo() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    "Erro ao atualizar rascunho",
                    List.of("Pelo menos um campo deve estar preenchido.")
            ));
        }

        try {
            Email rascunho = emailService.criarRascunho(dto, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensagem", "Rascunho criado",
                    "rascunho", Map.of(
                            "emailId", rascunho.getId(),
                            "assunto", rascunho.getSubject() != null ? rascunho.getSubject() : "",
                            "destinatario", dto.getDestinatario() != null ? dto.getDestinatario() : "",
                            "corpo", rascunho.getBody() != null ? rascunho.getBody() : ""
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    "Erro ao criar rascunho",
                    List.of(e.getMessage())
            ));
        }
    }*/

    @GetMapping
    public ResponseEntity<?> listarRascunhos(Authentication authentication) {
        try {
            String emailDoUsuario = authentication.getName();
            List<Email> rascunhos = emailService.listarRascunhosPorEmail(emailDoUsuario);

            List<Map<String, Object>> rascunhosFormatados = rascunhos.stream().map(r -> {
                Map<String, Object> rascunhoMap = new LinkedHashMap<>();
                rascunhoMap.put("rascunhoId", r.getId());

                if (r.getSubject() != null && !r.getSubject().isBlank()) {
                    rascunhoMap.put("assunto", r.getSubject());
                }
                if (r.getRecipientId() != null) {
                    String destinatarioEmail = getEmailDoDestinatario(String.valueOf(r.getRecipientId()));
                    if (destinatarioEmail != null && !destinatarioEmail.isBlank()) {
                        rascunhoMap.put("emailDestinatario", destinatarioEmail);
                    }
                }
                if (r.getBody() != null && !r.getBody().isBlank()) {
                    rascunhoMap.put("corpo", r.getBody());
                }

                return rascunhoMap;
            }).toList();

            return ResponseEntity.ok(Map.of(
                    "mensagem", "Rascunho localizado",
                    "rascunhos", rascunhosFormatados
            ));

        } catch (RuntimeException e) {
            if ("Nenhum rascunho encontrado para o usuário".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensagem", "Rascunho não encontrado"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensagem", "Erro ao listar rascunhos", "erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensagem", "Erro interno do servidor", "erro", e.getMessage()));
        }
    }

    private String getEmailDoDestinatario(String recipientId) {
        return userRepository.findById(recipientId)
                .map(User::getEmail)
                .orElse(null);
    }
}
