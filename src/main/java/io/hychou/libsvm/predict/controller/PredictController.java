package io.hychou.libsvm.predict.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.service.DataService;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.model.service.ModelService;
import io.hychou.libsvm.parameter.LibsvmPredictParameterEntity;
import io.hychou.libsvm.predict.service.PredictService;
import io.hychou.libsvm.prediction.entity.PredictionEntity;
import io.hychou.libsvm.prediction.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static io.hychou.common.Constant.SUCCESS_MESSAGE;

@RestController
public class PredictController {

    private final DataService dataService;
    private final ModelService modelService;
    private final PredictService predictService;
    private final PredictionService predictionService;

    @Autowired
    public PredictController(
            DataService dataService,
            ModelService modelService,
            PredictService predictService,
            PredictionService predictionService) {
        this.dataService = dataService;
        this.modelService = modelService;
        this.predictService = predictService;
        this.predictionService = predictionService;
    }

    @GetMapping(RequestMappingPath.SvmPredict)
    public MessageResponseEntity svmPredict(
            @PathVariable String dataName,
            @PathVariable("modelId") long modelId,
            @RequestParam(value = "probabilityEstimates", required = false) Boolean probabilityEstimates
    ) {
        LibsvmPredictParameterEntity libsvmPredictParameterEntity;
        if (Objects.nonNull(probabilityEstimates))
            libsvmPredictParameterEntity = new LibsvmPredictParameterEntity(probabilityEstimates);
        else
            libsvmPredictParameterEntity = new LibsvmPredictParameterEntity();
        try {
            DataEntity dataEntity = dataService.readDataByName(dataName);
            ModelEntity modelEntity = modelService.readModelById(modelId);
            PredictionEntity predictionEntity = predictService.svmPredict(dataEntity, modelEntity,
                    libsvmPredictParameterEntity);
            predictionEntity = predictionService.createPrediction(predictionEntity);
            return MessageResponseEntity.ok(predictionEntity.getId(), SUCCESS_MESSAGE);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
