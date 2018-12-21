package io.hychou.libsvm.model.controller;

final class RequestMappingPath {

    private RequestMappingPath() {
    }

    static final String ReadModelById = "/model/{id}";
    static final String CreateModel = "/model";
    static final String UpdateModelById = "/model/{id}";
    static final String DeleteModelById = "/model/{id}";
}
