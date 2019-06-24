package io.hychou.runnable.python.runner.service;

import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileEntity;

public interface PythonRunnerService {
    void run(PythonRunnerProfileEntity pythonRunnerProfileEntity);
}
