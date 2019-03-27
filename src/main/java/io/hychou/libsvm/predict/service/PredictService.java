package io.hychou.libsvm.predict.service;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.data.entity.DataEntity;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.parameter.LibsvmPredictParameterEntity;
import io.hychou.libsvm.prediction.entity.PredictionEntity;

public interface PredictService {
    PredictionEntity svmPredict(DataEntity dataEntity, ModelEntity modelEntity,
                                LibsvmPredictParameterEntity libsvmPredictParameterEntity) throws ServiceException;
}
