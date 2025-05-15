package com.auer.postfy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class RascunhoRequestDTO {

    @Size(max = 255, message = "Assunto deve ter no máximo 255 caracteres.")
    private String assunto;

    @Email(message = "Destinatário deve ser um email válido.")
    private String destinatario;

    private String corpo; // Remover a anotação @NotBlank

    // Getters e Setters
    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }
}
