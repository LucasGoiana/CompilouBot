package br.com.fiap.errors;

public class MovieNotFoundError extends Exception {

    public MovieNotFoundError(String message) {
        super(message);
    }
}
