package io.hychou.libsvm.prediction.controller;

final class RequestMappingPath {

    private RequestMappingPath() {
    }

    static final String ReadPredictionById = "/prediction/{id}";
    static final String CreatePrediction = "/prediction";
    static final String UpdatePredictionById = "/prediction/{id}";
    static final String DeletePredictionById = "/prediction/{id}";
}
