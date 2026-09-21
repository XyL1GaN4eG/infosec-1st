package ru.itmo.infosec.exceptions;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("Password is incorrect.");
    }
}