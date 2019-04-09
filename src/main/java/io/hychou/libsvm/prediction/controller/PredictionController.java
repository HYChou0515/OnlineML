package io.hychou.libsvm.prediction.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.libsvm.prediction.entity.PredictionEntity;
import io.hychou.libsvm.prediction.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.hychou.common.Constant.SUCCESS_MESSAGE;
import static io.hychou.common.util.TransformUtil.getBytesFrom;

@RestController
public class PredictionController {

    private final PredictionService predictionService;

    @Autowired
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping(RequestMappingPath.ReadPredictionById)
    public MessageResponseEntity readModelById(@PathVariable Long id) {
        try {
            PredictionEntity predictionEntity = predictionService.readPredictionById(id);
            Resource resource = new ByteArrayResource(predictionEntity.getDataBytes());
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).multipartFormData(predictionEntity.getFileName(), resource);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(value = RequestMappingPath.CreatePrediction,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponseEntity createModel(@RequestPart("blob") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            predictionService.createPrediction(new PredictionEntity(bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PutMapping(RequestMappingPath.UpdatePredictionById)
    public MessageResponseEntity updateModelById(@PathVariable Long id, @RequestPart("blob") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            predictionService.updatePrediction(new PredictionEntity(id, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @DeleteMapping(RequestMappingPath.DeletePredictionById)
    public MessageResponseEntity deleteModelById(@PathVariable Long id) {
        try {
            predictionService.deletePredictionById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
