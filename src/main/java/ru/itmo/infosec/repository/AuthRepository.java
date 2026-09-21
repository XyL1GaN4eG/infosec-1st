package ru.itmo.infosec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.infosec.entity.AuthModel;

@Repository
public interface AuthRepository extends JpaRepository<AuthModel, String> {
}