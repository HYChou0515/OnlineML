package io.hychou.libsvm.train.service;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.data.entity.DataEntity;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.parameter.LibsvmTrainParameterEntity;
import org.springframework.stereotype.Service;

@Service
public interface TrainService {
    ModelEntity svmTrain(DataEntity dataEntity, LibsvmTrainParameterEntity libsvmTrainParameterEntity) throws ServiceException;
}