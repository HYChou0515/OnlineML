package io.hychou.libsvm.model.controller;

final class RequestMappingPath {

    static final String ReadModelById = "/model/{id}";
    static final String CreateModel = "/model";
    static final String UpdateModelById = "/model/{id}";
    static final String DeleteModelById = "/model/{id}";
    private RequestMappingPath() {
    }
}
