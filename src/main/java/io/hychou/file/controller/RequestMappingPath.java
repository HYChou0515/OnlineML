package io.hychou.file.controller;

final class RequestMappingPath {
    static final String ReadAllFileInfo = "/file/info";
    static final String ReadFileInfoById = "/file/info/{id}";
    static final String ReadFileById = "/file/{id}";
    static final String CreateFileByName = "/file/{name}";
    static final String UpdateFileById = "/file/{id}";
    static final String DeleteFileById = "/file/{id}";

    private RequestMappingPath() {
    }
}