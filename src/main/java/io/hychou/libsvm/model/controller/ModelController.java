package io.hychou.libsvm.model.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.servererror.MultipartFileCannotGetBytesException;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.model.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static io.hychou.common.Constant.SUCCESS_MESSAGE;

@RestController
public class ModelController {

    private final ModelService modelService;

    @Autowired
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping(RequestMappingPath.ReadModelById)
    public MessageResponseEntity readModelById(@PathVariable Long id) {
        try {
            ModelEntity modelEntity = modelService.readModelById(id);
            Resource resource = new ByteArrayResource(modelEntity.getDataBytes());
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).multipartFormData(modelEntity.getFileName(), resource);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(value=RequestMappingPath.CreateModel,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponseEntity createModel(@RequestPart("blob") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            modelService.createModel(new ModelEntity(bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PutMapping(RequestMappingPath.UpdateModelById)
    public MessageResponseEntity updateModelById(@PathVariable Long id, @RequestPart("blob") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            modelService.updateModel(new ModelEntity(id, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @DeleteMapping(RequestMappingPath.DeleteModelById)
    public MessageResponseEntity deleteModelById(@PathVariable Long id) {
        try {
            modelService.deleteModelById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    private static byte[] getBytesFrom(MultipartFile multipartFile) throws ServiceException {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new MultipartFileCannotGetBytesException("Fail to transform multipartFile into byte array", e);
        }
    }
}
