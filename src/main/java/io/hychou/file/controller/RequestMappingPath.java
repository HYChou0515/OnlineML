package io.hychou.file.controller;

final class RequestMappingPath {
    static final String ReadAllFileInfo = "/file/info";
    static final String ReadFileInfoByName = "/file/info/{name}";
    static final String ReadFileByName = "/file/{name}";
    static final String CreateFileByName = "/file/{name}";
    static final String UpdateFileByName = "/file/{name}";
    static final String DeleteFileByName = "/file/{name}";

    private RequestMappingPath() {
    }
}