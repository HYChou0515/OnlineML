package io.hychou.libsvm.model.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.libsvm.model.dao.ModelEntityRepository;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.model.service.ModelService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ModelServiceImpl implements ModelService {
    private final ModelEntityRepository modelEntityRepository;
    private static final String ID_STRING = "Id";

    public ModelServiceImpl(ModelEntityRepository modelEntityRepository) {
        this.modelEntityRepository = modelEntityRepository;
    }

    @Override
    public ModelEntity readModelById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new ModelEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<ModelEntity> modelEntity = modelEntityRepository.findById(id);
        if (modelEntity.isPresent()) {
            return modelEntity.get();
        } else {
            throw new ElementNotExistException(new ModelEntity().getStringNotExistForParam(ID_STRING, id));
        }
    }

    @Override
    public ModelEntity createModel(ModelEntity modelEntity) throws ServiceException {
        if (modelEntity == null || modelEntity.getDataBytes() == null) {
            throw new NullParameterException(new ModelEntity().getStringCreateNull());
        }
        modelEntity = modelEntityRepository.save(modelEntity);
        return modelEntity;
    }

    @Override
    public ModelEntity updateModel(ModelEntity modelEntity) throws ServiceException {
        if (modelEntity == null || modelEntity.getId() == null || modelEntity.getDataBytes() == null) {
            throw new NullParameterException(new ModelEntity().getStringUpdateNull());
        }
        if (!modelEntityRepository.existsById(modelEntity.getId())) {
            throw new ElementNotExistException(new ModelEntity().getStringNotExistForParam(ID_STRING, modelEntity.getId()));
        }
        modelEntity = modelEntityRepository.save(modelEntity);
        return modelEntity;
    }

    @Override
    public void deleteModelById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new ModelEntity().getStringDeleteWithNullParam(ID_STRING));
        }
        if (!modelEntityRepository.existsById(id)) {
            throw new ElementNotExistException(new ModelEntity().getStringQueryWithNullParam(ID_STRING));
        }
        modelEntityRepository.deleteById(id);
    }
}
