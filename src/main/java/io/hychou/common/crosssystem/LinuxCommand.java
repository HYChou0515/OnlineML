package io.hychou.common.crosssystem;


import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinuxCommand implements Command {

    @Getter
    private List<String> command;

    public LinuxCommand(String... command) {
        this.command = new ArrayList<>(command.length);
        this.command.addAll(Arrays.asList(command));
    }
}
