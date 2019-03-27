package io.hychou.libsvm.predict.controller;

final class RequestMappingPath {

    private RequestMappingPath() {
    }
    static final String SvmPredict = "/predict/{dataName}/{modelId}";
}
