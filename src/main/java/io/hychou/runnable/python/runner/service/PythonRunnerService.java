package io.hychou.runnable.python.runner.service;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.runnable.python.runner.entity.PythonRunnerEntity;
import io.hychou.runnable.python.runner.entity.PythonRunnerInfo;

import java.util.List;

public interface PythonRunnerService {
    List<PythonRunnerInfo> listPythonRunnerInfo();

    PythonRunnerInfo readPythonRunnerInfoById(Long id) throws ServiceException;

    PythonRunnerEntity readPythonRunnerById(Long id) throws ServiceException;

    PythonRunnerEntity createPythonRunner(PythonRunnerEntity pythonRunnerEntity) throws ServiceException;

    void deletePythonRunnerById(Long id) throws ServiceException;
}
