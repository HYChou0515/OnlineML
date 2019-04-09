package io.hychou.common.exception.service.servererror;

import io.hychou.common.exception.service.ServiceException;
import org.springframework.http.HttpStatus;

public class FileSystemWriteException extends ServiceException {
    public FileSystemWriteException() {
        super();
    }
    public FileSystemWriteException(String message) {
        super(message);
    }
    public FileSystemWriteException(String message, Throwable cause) {
        super(message,cause);
    }
    public FileSystemWriteException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
