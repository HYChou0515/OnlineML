package io.hychou.common.exception.service.servererror;

import io.hychou.common.exception.service.ServiceException;
import org.springframework.http.HttpStatus;

public class FileSystemReadException extends ServiceException {
    public FileSystemReadException() {
        super();
    }
    public FileSystemReadException(String message) {
        super(message);
    }
    public FileSystemReadException(String message, Throwable cause) {
        super(message,cause);
    }
    public FileSystemReadException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
