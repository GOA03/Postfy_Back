package com.auer.postfy.dto;

public class RascunhoResponseDTO {
    private Long emaild;
    private String assunto;
    private String destinatario;
    private String corpo;

    public Long getEmaild() {
        return emaild;
    }

    public void setEmaild(Long emaild) {
        this.emaild = emaild;
    }

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
