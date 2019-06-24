package io.hychou.common.exception.server;

import java.util.List;

public class ShellCommandException extends ServerException {
    public ShellCommandException() {
        super();
    }

    public ShellCommandException(String message) {
        super(message);
    }

    public ShellCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShellCommandException(Throwable cause) {
        super(cause);
    }

    public static ShellCommandException invalidExitValue(String command, int exitValue) {
        return new ShellCommandException(String.format("Command \"%s\" exit with error code: %d", command, exitValue));
    }

    public static ShellCommandException invalidExitValue(List<String> command, int exitValue) {
        return new ShellCommandException(String.format("Command \"%s\" exit with error code: %d", String.join(" ", command), exitValue));
    }
}
