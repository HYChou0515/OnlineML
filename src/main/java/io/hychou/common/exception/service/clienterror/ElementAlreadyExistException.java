package io.hychou.common.exception.service.clienterror;

import io.hychou.common.exception.service.ServiceException;
import org.springframework.http.HttpStatus;

public class ElementAlreadyExistException extends ServiceException {
    public ElementAlreadyExistException() {
        super();
    }
    public ElementAlreadyExistException(String message) {
        super(message);
    }
    public ElementAlreadyExistException(String message, Throwable cause) {
        super(message,cause);
    }
    public ElementAlreadyExistException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
