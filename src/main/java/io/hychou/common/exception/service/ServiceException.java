package io.hychou.common.exception.service;

import io.hychou.common.MessageResponseEntity;
import org.springframework.http.HttpStatus;

public abstract class ServiceException extends Exception {
    public ServiceException() {
        super();
    }
    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    public ServiceException(Throwable cause) {
        super(cause);
    }

    public MessageResponseEntity getMessageResponseEntity() {
        return MessageResponseEntity.status(this.getHttpStatus(), this.getMessage()).build();
    }

    abstract public HttpStatus getHttpStatus();
}
