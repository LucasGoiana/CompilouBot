package br.com.fiap.errors;

public class ClientRequestError  extends InternalError{
    public ClientRequestError(String message) {
        super(message);
    }
}
