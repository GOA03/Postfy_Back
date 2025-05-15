package com.auer.postfy.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ErrorResponseDTO {
    private String mensagem;
    private List<Map<String, String>> detalhes;

    public ErrorResponseDTO(String mensagem, List<String> detalhes) {
        this.mensagem = mensagem;
        this.detalhes = detalhes.stream()
                .map(erro -> Map.of("erro", erro))
                .collect(Collectors.toList());
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public List<Map<String, String>> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<Map<String, String>> detalhes) {
        this.detalhes = detalhes;
    }
}
