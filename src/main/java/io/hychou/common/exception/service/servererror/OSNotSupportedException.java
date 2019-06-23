package io.hychou.common.exception.service.servererror;

import io.hychou.common.exception.service.ServiceException;
import org.springframework.http.HttpStatus;

public class OSNotSupportedException extends ServiceException {
    public OSNotSupportedException() {
        super();
    }

    public OSNotSupportedException(String message) {
        super(message);
    }

    public OSNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public OSNotSupportedException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
