package ru.itmo.infosec.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.infosec.dto.CredentialsDto;
import ru.itmo.infosec.service.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String register(@RequestBody @Valid CredentialsDto request) {
        return authService.register(request);
    }

    @PostMapping("/auth/login")
    public String login(@RequestBody @Valid CredentialsDto request) {
        return authService.login(request);
    }
}