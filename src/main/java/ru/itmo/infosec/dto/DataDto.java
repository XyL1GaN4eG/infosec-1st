package ru.itmo.infosec.dto;

import jakarta.validation.constraints.NotBlank;

public record DataDto(
    @NotBlank
    String message
) {
}