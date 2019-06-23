package io.hychou.runnable.python.runner.profile.service;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileEntity;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileInfo;

import java.util.List;

public interface PythonRunnerProfileService {
    List<PythonRunnerProfileInfo> listPythonRunnerProfileInfo();

    PythonRunnerProfileInfo readPythonRunnerProfileInfoById(Long id) throws ServiceException;

    PythonRunnerProfileEntity readPythonRunnerProfileById(Long id) throws ServiceException;

    PythonRunnerProfileEntity createPythonRunnerProfile(PythonRunnerProfileEntity pythonRunnerProfileEntity) throws ServiceException;

    void deletePythonRunnerProfileById(Long id) throws ServiceException;
}
