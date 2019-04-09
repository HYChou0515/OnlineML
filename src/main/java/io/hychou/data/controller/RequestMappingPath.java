package io.hychou.data.controller;

final class RequestMappingPath {
    private RequestMappingPath() {
    }

    static final String ReadAllDataInfo = "/data/info";
    static final String ReadDataByName = "/data/{name}";
    static final String CreateDataByName = "/data/{name}";
    static final String UpdateDataByName = "/data/{name}";
    static final String DeleteDataByName = "/data/{name}";
}