package io.hychou.common.exception.service.clienterror;

import io.hychou.common.exception.service.ServiceException;
import org.springframework.http.HttpStatus;

public class ElementNotExistException extends ServiceException {
    public ElementNotExistException() {
        super();
    }
    public ElementNotExistException(String message) {
        super(message);
    }
    public ElementNotExistException(String message, Throwable cause) {
        super(message,cause);
    }
    public ElementNotExistException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
