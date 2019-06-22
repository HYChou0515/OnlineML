package io.hychou.runnable.python.runner.controller;

final class RequestMappingPath {
    static final String ReadPythonRunnerInfo = "/pythonrunner/info";
    static final String ReadPythonRunnerInfoById = "/pythonrunner/info/{id}";
    static final String ReadPythonRunnerById = "/pythonrunner/{id}";
    static final String CreatePythonRunner = "/pythonrunner/{pythonCodeId}/{environmentId}";
    static final String UpdatePythonRunnerById = "/pythonrunner/{id}";
    static final String DeletePythonRunnerById = "/pythonrunner/{id}";

    private RequestMappingPath() {
    }
}