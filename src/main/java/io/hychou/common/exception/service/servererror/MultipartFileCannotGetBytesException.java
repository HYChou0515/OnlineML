package io.hychou.common.exception.service.servererror;

import io.hychou.common.exception.service.ServiceException;
import org.springframework.http.HttpStatus;

public class MultipartFileCannotGetBytesException extends ServiceException {
    public MultipartFileCannotGetBytesException() {
        super();
    }
    public MultipartFileCannotGetBytesException(String message) {
        super(message);
    }
    public MultipartFileCannotGetBytesException(String message, Throwable cause) {
        super(message,cause);
    }
    public MultipartFileCannotGetBytesException(Throwable cause) {
        super(cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
