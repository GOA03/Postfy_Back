package com.auer.postfy.dto;

import jakarta.validation.Valid;

public class UsuarioWrapperDTO {
    @Valid
    private UserRequestDTO usuario;

    public UserRequestDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UserRequestDTO usuario) {
        this.usuario = usuario;
    }
}
