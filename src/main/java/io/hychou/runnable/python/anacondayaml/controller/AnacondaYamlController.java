package io.hychou.runnable.python.anacondayaml.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import io.hychou.runnable.python.anacondayaml.service.AnacondaYamlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static io.hychou.common.Constant.SUCCESS_MESSAGE;
import static io.hychou.common.util.TransformUtil.getBytesFrom;

@RestController
public class AnacondaYamlController {

    private final AnacondaYamlService anacondaYamlService;

    @Autowired
    public AnacondaYamlController(AnacondaYamlService anacondaYamlService) {
        this.anacondaYamlService = anacondaYamlService;
    }

    @GetMapping(RequestMappingPath.ReadAllAnacondaYamlInfo)
    public MessageResponseEntity readAllAnacondaYamlInfo() {
        List<AnacondaYamlInfo> anacondaYamlInfoList = anacondaYamlService.listAnacondaYamlInfo();
        return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(anacondaYamlInfoList);
    }

    @GetMapping(RequestMappingPath.ReadAnacondaYamlInfoByName)
    public MessageResponseEntity readAnacondaYamlInfoByName(@PathVariable String name) {
        try {
            AnacondaYamlInfo anacondaYamlInfo = anacondaYamlService.readAnacondaYamlInfoByName(name);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(anacondaYamlInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadAnacondaYamlByName)
    public MessageResponseEntity readAnacondaYamlByName(@PathVariable String name) {
        try {
            AnacondaYamlEntity anacondaYamlEntity = anacondaYamlService.readAnacondaYamlByName(name);
            Resource resource = new ByteArrayResource(anacondaYamlEntity.getAnacondaYamlBytes());
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).multipartFormData(anacondaYamlEntity.getName(), resource);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(value = RequestMappingPath.CreateAnacondaYamlByName,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponseEntity createAnacondaYamlByName(@PathVariable String name, @RequestPart("blob") MultipartFile multipartAnacondaYaml) {
        try {
            byte[] bytes = getBytesFrom(multipartAnacondaYaml);
            anacondaYamlService.createAnacondaYaml(new AnacondaYamlEntity(name, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PutMapping(RequestMappingPath.UpdateAnacondaYamlByName)
    public MessageResponseEntity updateAnacondaYamlByName(@PathVariable String name, @RequestPart("blob") MultipartFile multipartAnacondaYaml) {
        try {
            byte[] bytes = getBytesFrom(multipartAnacondaYaml);
            anacondaYamlService.updateAnacondaYaml(new AnacondaYamlEntity(name, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @DeleteMapping(RequestMappingPath.DeleteAnacondaYamlByName)
    public MessageResponseEntity deleteAnacondaYamlByName(@PathVariable String name) {
        try {
            anacondaYamlService.deleteAnacondaYamlByName(name);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
