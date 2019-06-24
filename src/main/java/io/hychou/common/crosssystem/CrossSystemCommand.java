package io.hychou.common.crosssystem;

import io.hychou.common.exception.server.OSNotSupportedException;
import io.hychou.common.exception.server.ServerException;
import io.hychou.common.exception.server.ShellCommandException;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;

import java.io.IOException;

import static org.apache.commons.lang.SystemUtils.OS_NAME;
import static org.slf4j.LoggerFactory.getLogger;

public class CrossSystemCommand {
    public final static WindowsCommand WINDOWS_DUMMY_COMMAND = new WindowsCommand("break");
    public final static LinuxCommand LINUX_DUMMY_COMMAND = new LinuxCommand("break");
    private final static Logger logger = getLogger(CrossSystemCommand.class);
    private final LinuxCommand linuxCommand;
    private final WindowsCommand windowsCommand;

    public CrossSystemCommand(LinuxCommand linuxCommand, WindowsCommand windowsCommand) {
        this.linuxCommand = linuxCommand;
        this.windowsCommand = windowsCommand;
    }

    public ProcessBuilder build() throws OSNotSupportedException {
        ProcessBuilder builder = new ProcessBuilder();
        if (SystemUtils.IS_OS_LINUX) {
            builder.command(linuxCommand.getCommand());
        } else if (SystemUtils.IS_OS_WINDOWS) {
            builder.command(windowsCommand.getCommand());
        } else {
            throw new OSNotSupportedException("Server Operating SupportSystem \"" + OS_NAME + "\" is not supported");
        }
        return builder;
    }

    public Process startAndWait() throws ServerException, InterruptedException, IOException {
        ProcessBuilder builder = this.build();
        logger.info("start process with command: \"{}\"", builder.command());
        Process process = builder.start();
        if (process.waitFor() != 0) {
            throw ShellCommandException.invalidExitValue(builder.command(), process.exitValue());
        }
        return process;
    }
}
