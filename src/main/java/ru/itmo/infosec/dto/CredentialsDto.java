package ru.itmo.infosec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CredentialsDto(
    @NotBlank
    @Size(min = 5, max = 50)
    String login,
    @NotBlank
    @Size(min = 5, max = 50)
    String password
) {
}