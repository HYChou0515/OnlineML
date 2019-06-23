package io.hychou.runnable.python.runner.profile.controller;

final class RequestMappingPath {
    static final String ReadPythonRunnerProfileInfo = "/pythonrunnerprofile/info";
    static final String ReadPythonRunnerProfileInfoById = "/pythonrunnerprofile/info/{id}";
    static final String ReadPythonRunnerProfileById = "/pythonrunnerprofile/{id}";
    static final String CreatePythonRunnerProfile = "/pythonrunnerprofile/{pythonCodeId}/{environmentId}";
    static final String UpdatePythonRunnerProfileById = "/pythonrunnerprofile/{id}";
    static final String DeletePythonRunnerProfileById = "/pythonrunnerprofile/{id}";

    private RequestMappingPath() {
    }
}