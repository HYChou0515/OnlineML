package io.hychou.runnable.python.anacondayaml.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.datastructure.blob.service.BlobService;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
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

    private final BlobService<AnacondaYamlEntity, AnacondaYamlInfo> anacondaYamlService;

    @Autowired
    public AnacondaYamlController(BlobService<AnacondaYamlEntity, AnacondaYamlInfo> anacondaYamlService) {
        this.anacondaYamlService = anacondaYamlService;
    }

    @GetMapping(RequestMappingPath.ReadAllAnacondaYamlInfo)
    public MessageResponseEntity readAllAnacondaYamlInfo(@RequestParam(value = "name", required = false, defaultValue = "") String name) {
        if (name.isEmpty()) {
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(anacondaYamlService.listBlobInfo());
        }
        try {
            List<AnacondaYamlInfo> fileInfo = anacondaYamlService.readBlobInfoByName(name);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(fileInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadAnacondaYamlInfoById)
    public MessageResponseEntity readAnacondaYamlInfoById(@PathVariable Long id) {
        try {
            AnacondaYamlInfo fileInfo = anacondaYamlService.readBlobInfoById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(fileInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadAnacondaYamlById)
    public MessageResponseEntity readAnacondaYamlById(@PathVariable Long id) {
        try {
            AnacondaYamlEntity fileEntity = anacondaYamlService.readBlobById(id);
            Resource resource = new ByteArrayResource(fileEntity.getBlobBytes());
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).multipartFormData(fileEntity.getName(), resource);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(value = RequestMappingPath.CreateAnacondaYamlByName,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponseEntity createAnacondaYamlByName(@PathVariable String name, @RequestPart("file") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            anacondaYamlService.createBlob(new AnacondaYamlEntity(name, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PutMapping(RequestMappingPath.UpdateAnacondaYamlById)
    public MessageResponseEntity updateAnacondaYamlById(@PathVariable Long id, @RequestPart("file") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            anacondaYamlService.updateBlobById(id, bytes);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @DeleteMapping(RequestMappingPath.DeleteAnacondaYamlById)
    public MessageResponseEntity deleteAnacondaYamlById(@PathVariable Long id) {
        try {
            anacondaYamlService.deleteBlobById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
