package io.hychou.runnable.python.runner.entity;

import io.hychou.file.entity.FileInfo;
import io.hychou.runnable.python.runner.RunnerStateEnum;

import java.util.Date;
import java.util.List;

public interface PythonRunnerInfo {
    Long getId();

    TimeDependentFileInfo getPythonCode();

    List<TimeDependentFileInfo> getDependencies();

    TimeDependentAnacondaYamlInfo getEnvironment();

    RunnerStateEnum getState();

    Date getCreatedTimestamp();

    Date getPreparingTimestamp();

    Date getRunningTimestamp();

    Date getCleaningTimestamp();

    Date getFinishedTimestamp();

    FileInfo getResult();

    String getSummary();

    List<String> getErrorMessages();
}
