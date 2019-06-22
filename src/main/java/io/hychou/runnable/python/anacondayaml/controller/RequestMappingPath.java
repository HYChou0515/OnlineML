package io.hychou.runnable.python.anacondayaml.controller;

final class RequestMappingPath {
    static final String ReadAllAnacondaYamlInfo = "/anacondayaml/info";
    static final String ReadAnacondaYamlInfoByName = "/anacondayaml/info/{name}";
    static final String ReadAnacondaYamlByName = "/anacondayaml/{name}";
    static final String CreateAnacondaYamlByName = "/anacondayaml/{name}";
    static final String UpdateAnacondaYamlByName = "/anacondayaml/{name}";
    static final String DeleteAnacondaYamlByName = "/anacondayaml/{name}";

    private RequestMappingPath() {
    }
}