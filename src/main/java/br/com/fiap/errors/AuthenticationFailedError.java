package br.com.fiap.errors;

public class AuthenticationFailedError extends Exception{

    public AuthenticationFailedError(String message) {
        super(message);
    }
}
