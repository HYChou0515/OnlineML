package io.hychou.runnable.python.anacondayaml.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.config.RunnablePathProperties;
import io.hychou.runnable.python.anacondayaml.AnacondaYamlService;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static io.hychou.common.Constant.EMPTY_STRING;
import static io.hychou.common.Constant.SUCCESS_MESSAGE;
import static io.hychou.common.utilities.TransformUtilities.getBytesFrom;

@RestController
public class AnacondaYamlController {

    private final AnacondaYamlService<AnacondaYamlInfo> anacondaYamlService;
    private final RunnablePathProperties runnablePathProperties;

    @Autowired
    public AnacondaYamlController(AnacondaYamlService<AnacondaYamlInfo> anacondaYamlService, RunnablePathProperties runnablePathProperties) {
        this.anacondaYamlService = anacondaYamlService;
        this.runnablePathProperties = runnablePathProperties;
    }

    @GetMapping(RequestMappingPath.ReadAllAnacondaYamlInfo)
    public MessageResponseEntity readAllAnacondaYamlInfo(@RequestParam(value = "name", required = false, defaultValue = EMPTY_STRING) String name) {
        if (name.isEmpty()) {
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(anacondaYamlService.listInfo());
        }
        try {
            List<AnacondaYamlInfo> anacondaYamlInfoList = anacondaYamlService.readInfoByName(name);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(anacondaYamlInfoList);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadAnacondaYamlInfoById)
    public MessageResponseEntity readAnacondaYamlInfoById(@PathVariable Long id) {
        try {
            AnacondaYamlInfo anacondaYamlInfo = anacondaYamlService.readInfoById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(anacondaYamlInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadAnacondaYamlById)
    public MessageResponseEntity readAnacondaYamlById(@PathVariable Long id) {
        try {
            AnacondaYamlEntity anacondaYamlEntity = anacondaYamlService.readById(id);
            Resource resource = new ByteArrayResource(anacondaYamlEntity.getBlobBytes());
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).multipartFormData(anacondaYamlEntity.getName(), resource);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(value = RequestMappingPath.CreateAnacondaYamlByName,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponseEntity createAnacondaYamlByName(@PathVariable String name, @RequestPart("anaconda_yaml") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            AnacondaYamlEntity anacondaYamlEntity = anacondaYamlService.create(new AnacondaYamlEntity(name, bytes, runnablePathProperties.getAnacondaEnvBaseDir()));
            AnacondaYamlInfo anacondaYamlInfo = anacondaYamlService.readInfoById(anacondaYamlEntity.getId());
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(anacondaYamlInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PutMapping(RequestMappingPath.UpdateAnacondaYamlById)
    public MessageResponseEntity updateAnacondaYamlById(@PathVariable Long id, @RequestPart("anaconda_yaml") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            anacondaYamlService.updateById(id, bytes);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @DeleteMapping(RequestMappingPath.DeleteAnacondaYamlById)
    public MessageResponseEntity deleteAnacondaYamlById(@PathVariable Long id) {
        try {
            anacondaYamlService.deleteById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
