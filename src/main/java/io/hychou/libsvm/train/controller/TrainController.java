package io.hychou.libsvm.train.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.ServiceException;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.model.service.ModelService;
import io.hychou.libsvm.parameter.KernelTypeEnum;
import io.hychou.libsvm.parameter.LibsvmParameterEntity;
import io.hychou.libsvm.parameter.SvmTypeEnum;
import io.hychou.libsvm.train.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static io.hychou.common.Constant.SUCCESS_MESSAGE;

@RestController
public class TrainController {

    private final TrainService trainService;
    private final ModelService modelService;

    @Autowired
    public TrainController(TrainService trainService, ModelService modelService) {
        this.trainService = trainService;
        this.modelService = modelService;
    }

    @GetMapping(RequestMappingPath.SvmTrain)
    public MessageResponseEntity svmTrain(
            @PathVariable String dataName,
            @RequestParam(value = "svmType", required=false) SvmTypeEnum svmType,
            @RequestParam(value = "kernelType", required=false) KernelTypeEnum kernelType,
            @RequestParam(value = "degree", required=false) Integer degree,
            @RequestParam(value = "gamma", required=false) Double gamma,
            @RequestParam(value = "coef0", required=false) Double coef0,
            @RequestParam(value = "cacheSize", required=false) Double cacheSize,
            @RequestParam(value = "eps", required=false) Double eps,
            @RequestParam(value = "c", required=false) Double c,
            @RequestParam(value = "nu", required=false) Double nu,
            @RequestParam(value = "p", required=false) Double p,
            @RequestParam(value = "shrinking", required=false) Boolean shrinking,
            @RequestParam(value = "probability", required=false) Boolean probability
    ) {
        LibsvmParameterEntity libsvmParameterEntity = LibsvmParameterEntity.build()
                .svmType(svmType).kernelType(kernelType).degree(degree)
                .gamma(gamma).coef0(coef0).cacheSize(cacheSize)
                .eps(eps).c(c).nu(nu).p(p).shrinking(shrinking).probability(probability).done();
        try {
            ModelEntity modelEntity = trainService.svmTrain(dataName, libsvmParameterEntity);
            modelEntity = modelService.createModel(modelEntity);
            return MessageResponseEntity.ok(modelEntity.getId(), SUCCESS_MESSAGE);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
