package io.hychou.common.exception.service.servererror;

import io.hychou.common.exception.service.ServiceException;
import org.springframework.http.HttpStatus;

public class ServerIOException extends ServiceException {
    public ServerIOException() {
        super();
    }
    public ServerIOException(String message) {
        super(message);
    }
    public ServerIOException(String message, Throwable cause) {
        super(message,cause);
    }
    public ServerIOException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
