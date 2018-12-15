package io.hychou.libsvm.train.service.impl;

import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.parameter.LibsvmParameterEntity;
import io.hychou.libsvm.train.service.TrainService;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import static org.apache.logging.log4j.LogManager.getLogger;

@Service
public class TrainServiceImpl implements TrainService {
    @Override
    public ModelEntity svmTrain(String dataName, LibsvmParameterEntity libsvmParameterEntity) {
        Logger logger = getLogger(this.getClass());
        logger.warn(libsvmParameterEntity.toString());
        logger.warn(dataName);
        ModelEntity a9aModel = new ModelEntity();
        a9aModel.setId(1L);
        a9aModel.setDataBytes("This is a9a model".getBytes());
        return a9aModel;
    }
}
