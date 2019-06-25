package io.hychou.runnable.python.runner.profile.entity;

import io.hychou.file.entity.FileInfo;
import io.hychou.runnable.python.runner.RunnerStateEnum;
import io.hychou.runnable.timedependent.entity.TimeDependentInfo;

import java.util.Date;
import java.util.List;

public interface PythonRunnerProfileInfo {
    Long getId();

    TimeDependentInfo getPythonCode();

    List<TimeDependentInfo> getDependencies();

    TimeDependentInfo getEnvironment();

    RunnerStateEnum getState();

    Date getCreatedTimestamp();

    Date getPreparingTimestamp();

    Date getRunningTimestamp();

    Date getCleaningTimestamp();

    Date getFinishedTimestamp();

    List<FileInfo> getResult();

    String getSummary();

    List<String> getErrorMessages();
}
