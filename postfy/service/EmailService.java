package com.auer.postfy.service;

import com.auer.postfy.dto.RascunhoRequestDTO;
import com.auer.postfy.entity.Email;
import com.auer.postfy.entity.User;
import com.auer.postfy.enums.EmailStatus;
import com.auer.postfy.repository.EmailRepository;
import com.auer.postfy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService {

    private final EmailRepository repository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public EmailService(EmailRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Email criarRascunho(RascunhoRequestDTO dto, String emailDoUsuarioLogado) {
        // Buscar o usuário autenticado pelo e-mail
        User remetente = userRepository.findByEmail(emailDoUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário remetente não encontrado"));

        Email rascunho = new Email();
        rascunho.setSenderId(remetente.getId());

        // Campos opcionais
        if (dto.getAssunto() != null) {
            rascunho.setSubject(dto.getAssunto());
        }
        if (dto.getCorpo() != null) {
            rascunho.setBody(dto.getCorpo());
        }
        if (dto.getDestinatario() != null && !dto.getDestinatario().isBlank()) {
            userRepository.findByEmail(dto.getDestinatario()).ifPresent(destinatario ->
                    rascunho.setRecipientId(destinatario.getId())
            );
        }

        rascunho.setStatus(EmailStatus.DRAFT);
        rascunho.setCreatedAt(LocalDateTime.now());
        rascunho.setUpdatedAt(LocalDateTime.now());

        try {
            return repository.save(rascunho);
        } catch (Exception e) {
            logger.error("Erro ao criar rascunho: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar o rascunho");
        }
    }

    /*public Email atualizarRascunho(RascunhoRequestDTO dto, String emailDoUsuarioLogado) {
        // Buscar o usuário autenticado pelo e-mail
        User remetente = userRepository.findByEmail(emailDoUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário remetente não encontrado"));



        try {
            return repository.save(rascunho);
        } catch (Exception e) {
            logger.error("Erro ao criar rascunho: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar o rascunho");
        }
    }*/

    public List<Email> listarRascunhosPorEmail(String emailDoUsuario) {
        User usuario = userRepository.findByEmail(emailDoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Email> rascunhos = repository.findBySenderIdAndStatus(usuario.getId(), EmailStatus.DRAFT);

        if (rascunhos.isEmpty()) {
            throw new RuntimeException("Nenhum rascunho encontrado para o usuário");
        }
        return rascunhos;
    }

}
