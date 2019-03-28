package io.hychou.common.exception.service.servererror;

import io.hychou.common.exception.service.ServiceException;
import org.springframework.http.HttpStatus;

public class SvmLoadModelException extends ServiceException {
    public SvmLoadModelException() {
        super();
    }
    public SvmLoadModelException(String message) {
        super(message);
    }
    public SvmLoadModelException(String message, Throwable cause) {
        super(message,cause);
    }
    public SvmLoadModelException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }
}
