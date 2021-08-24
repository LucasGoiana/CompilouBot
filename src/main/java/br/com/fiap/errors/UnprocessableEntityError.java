package br.com.fiap.errors;

public class UnprocessableEntityError extends Exception{
    public UnprocessableEntityError(String message) {
        super(message);
    }
}
