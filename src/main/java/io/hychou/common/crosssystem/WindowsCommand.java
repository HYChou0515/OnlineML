package io.hychou.common.crosssystem;


import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WindowsCommand implements Command {

    @Getter
    private List<String> command;

    public WindowsCommand(String... command) {
        this.command = new ArrayList<>(command.length);
        this.command.addAll(Arrays.asList(command));
    }
}