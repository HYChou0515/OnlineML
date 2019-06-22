package io.hychou.runnable.python.anacondayaml.service;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;

import java.util.List;

public interface AnacondaYamlService {
    List<AnacondaYamlInfo> listAnacondaYamlInfo();

    AnacondaYamlInfo readAnacondaYamlInfoByName(String name) throws ServiceException;

    AnacondaYamlEntity readAnacondaYamlByName(String name) throws ServiceException;

    AnacondaYamlEntity createAnacondaYaml(AnacondaYamlEntity anacondaYamlEntity) throws ServiceException;

    AnacondaYamlEntity updateAnacondaYaml(AnacondaYamlEntity anacondaYamlEntity) throws ServiceException;

    void deleteAnacondaYamlByName(String name) throws ServiceException;
}
