package io.hychou.common.exception.service.clienterror;

import io.hychou.common.exception.service.ServiceException;
import org.springframework.http.HttpStatus;

public class IllegalParameterException extends ServiceException {
    public IllegalParameterException() {
        super();
    }
    public IllegalParameterException(String message) {
        super(message);
    }
    public IllegalParameterException(String message, Throwable cause) {
        super(message,cause);
    }
    public IllegalParameterException(Throwable cause) {
        super(cause);
    }
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
