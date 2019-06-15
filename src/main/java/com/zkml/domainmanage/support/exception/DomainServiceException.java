package com.zkml.domainmanage.support.exception;

public class DomainServiceException extends RuntimeException{

    public DomainServiceException() {
        super();
    }

    public DomainServiceException(String message) {
        super(message);
    }

    public DomainServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainServiceException(Throwable cause) {
        super(cause);
    }
}
