package io.hychou.common.exception.clienterror;

import io.hychou.common.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class NullParameterException extends ServiceException {
    public NullParameterException() {
        super();
    }
    public NullParameterException(String message) {
        super(message);
    }
    public NullParameterException(String message, Throwable cause) {
        super(message,cause);
    }
    public NullParameterException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.LENGTH_REQUIRED;
    }
}
