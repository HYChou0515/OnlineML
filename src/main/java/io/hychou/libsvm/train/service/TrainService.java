package io.hychou.libsvm.train.service;

import io.hychou.common.exception.ServiceException;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.parameter.LibsvmParameterEntity;
import org.springframework.stereotype.Service;

@Service
public interface TrainService {
    ModelEntity svmTrain(String dataName, LibsvmParameterEntity libsvmParameterEntity) throws ServiceException;
}
