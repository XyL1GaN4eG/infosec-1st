package ru.itmo.infosec.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "auth_table")
public class AuthModel {
    @Id
    String login;
    String password;

    public AuthModel() {
    }

    public AuthModel(String login, String password) {
        this.login = login;
        this.password = password;
    }
}