package io.hychou.runnable.python.anacondayaml.controller;

final class RequestMappingPath {
    static final String ReadAllAnacondaYamlInfo = "/anacondayaml/info";
    static final String ReadAnacondaYamlInfoById = "/anacondayaml/info/{id}";
    static final String ReadAnacondaYamlById = "/anacondayaml/{id}";
    static final String CreateAnacondaYamlByName = "/anacondayaml/{name}";
    static final String UpdateAnacondaYamlById = "/anacondayaml/{id}";
    static final String DeleteAnacondaYamlById = "/anacondayaml/{id}";

    private RequestMappingPath() {
    }
}