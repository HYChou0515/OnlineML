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

    public ModelServiceImpl(ModelEntityRepository modelEntityRepository) {
        this.modelEntityRepository = modelEntityRepository;
    }

    private static String modelWithIdDoesNotExist(Long id) {
           return "Model with id="+id+" does not exist";
    }

    @Override
    public ModelEntity readModelById(Long id) throws ServiceException {
        if(id == null) {
            throw new NullParameterException("Trying to query with null id");
        }
        Optional<ModelEntity> modelEntity = modelEntityRepository.findById(id);
        if(modelEntity.isPresent()) {
            return modelEntity.get();
        } else {
            throw new ElementNotExistException(modelWithIdDoesNotExist(id));
        }
    }

    @Override
    public ModelEntity createModel(ModelEntity modelEntity) throws ServiceException {
        if (modelEntity == null || modelEntity.getDataBytes() == null) {
            throw new NullParameterException("Trying to create null model");
        }
        modelEntity = modelEntityRepository.save(modelEntity);
        return modelEntity;
    }

    @Override
    public ModelEntity updateModel(ModelEntity modelEntity) throws ServiceException {
        if (modelEntity == null || modelEntity.getId() == null || modelEntity.getDataBytes() == null) {
            throw new NullParameterException("Trying to update null model");
        }
        if( ! modelEntityRepository.existsById(modelEntity.getId())) {
            throw new ElementNotExistException(modelWithIdDoesNotExist(modelEntity.getId()));
        }
        modelEntity = modelEntityRepository.save(modelEntity);
        return modelEntity;
    }

    @Override
    public void deleteModelById(Long id) throws ServiceException {
        if(id == null) {
            throw new NullParameterException("Trying to delete model with null id");
        }
        if( ! modelEntityRepository.existsById(id)) {
            throw new ElementNotExistException(modelWithIdDoesNotExist(id));
        }
        modelEntityRepository.deleteById(id);
    }
}
