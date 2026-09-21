package ru.itmo.infosec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import ru.itmo.infosec.dto.CredentialsDto;
import ru.itmo.infosec.entity.AuthModel;
import ru.itmo.infosec.exceptions.IncorrectPasswordException;
import ru.itmo.infosec.exceptions.UserAlreadyExistsException;
import ru.itmo.infosec.exceptions.UserNotFoundException;
import ru.itmo.infosec.repository.AuthRepository;
import ru.itmo.infosec.utils.JwtUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public String register(CredentialsDto credentialsDto) {
        Optional<AuthModel> user = authRepository.findById(credentialsDto.login());
        if (user.isPresent())
            throw new UserAlreadyExistsException("User with name " + credentialsDto.login() + " already exists!");

        AuthModel authModel = new AuthModel(
                HtmlUtils.htmlEscape(credentialsDto.login()),
                passwordEncoder.encode(credentialsDto.password())
        );
        authRepository.save(authModel);
        return jwtUtils.generateJwtToken(authModel.getLogin());
    }

    public String login(CredentialsDto credentialsDto) {
        AuthModel user = authRepository.findById(credentialsDto.login())
                .orElseThrow(() -> new UserNotFoundException("User with name " + credentialsDto.login() + " not found!"));
        if (!passwordEncoder.matches(credentialsDto.password(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }
        return jwtUtils.generateJwtToken(user.getLogin());
    }
}