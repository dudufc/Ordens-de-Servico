package br.com.cftecnologia.ordens.repository;

public class RepositoryException extends RuntimeException {

    public RepositoryException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

