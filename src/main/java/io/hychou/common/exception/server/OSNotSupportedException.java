package io.hychou.common.exception.server;

public class OSNotSupportedException extends ServerException {
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
}
