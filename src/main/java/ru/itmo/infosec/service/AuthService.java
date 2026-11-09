package ru.itmo.infosec.service;

import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.infosec.dto.CredentialsDto;
import ru.itmo.infosec.entity.AuthModel;
import ru.itmo.infosec.exceptions.IncorrectPasswordException;
import ru.itmo.infosec.exceptions.UserAlreadyExistsException;
import ru.itmo.infosec.repository.AuthRepository;
import ru.itmo.infosec.utils.JwtUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public String register(CredentialsDto credentialsDto) {
        Optional<AuthModel> user = authRepository.findById(credentialsDto.login());
        if (user.isPresent())
            throw new UserAlreadyExistsException("User already exists");

        AuthModel authModel = new AuthModel(
                credentialsDto.login(),
                passwordEncoder.encode(credentialsDto.password())
        );
        try {
            entityManager.persist(authModel);
            entityManager.flush();
        } catch (PersistenceException e) {
            if (!(e instanceof org.hibernate.exception.ConstraintViolationException)) {
                throw e;
            }
            throw new UserAlreadyExistsException("User already exists");
        }
        return jwtUtils.generateJwtToken(authModel.getLogin());
    }

    public String login(CredentialsDto credentialsDto) {
        AuthModel user = authRepository.findById(credentialsDto.login())
                .orElseThrow(IncorrectPasswordException::new);
        if (!passwordEncoder.matches(credentialsDto.password(), user.getPassword())) {
            throw new IncorrectPasswordException();
        }
        return jwtUtils.generateJwtToken(user.getLogin());
    }
}
