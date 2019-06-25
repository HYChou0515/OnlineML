package io.hychou.libsvm.model.service;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.libsvm.model.entity.ModelEntity;

public interface ModelService {

    ModelEntity readModelById(Long id) throws ServiceException;

    ModelEntity createModel(ModelEntity modelEntity) throws ServiceException;

    ModelEntity updateModel(ModelEntity modelEntity) throws ServiceException;

    void deleteModelById(Long id) throws ServiceException;
}
