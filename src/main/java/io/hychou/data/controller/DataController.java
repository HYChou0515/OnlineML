package io.hychou.data.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.ServiceException;
import io.hychou.common.exception.servererror.MultipartFileCannotGetBytesException;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.entity.DataInfo;
import io.hychou.data.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static io.hychou.common.Constant.SUCCESS_MESSAGE;

@RestController
public class DataController {

    private final DataService dataService;

    @Autowired
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping(RequestMappingPath.ReadAllDataInfo)
    public MessageResponseEntity readAllDataInfo() {
        List<DataInfo> dataInfoList = dataService.listDataInfo();
        return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(dataInfoList);
    }

    @GetMapping(RequestMappingPath.ReadDataByName)
    public MessageResponseEntity readDataByName(@PathVariable String name) {
        try {
            DataEntity dataEntity = dataService.readDataByName(name);
            Resource resource = new ByteArrayResource(dataEntity.getDataBytes());
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).multipartFormData(dataEntity.getName(), resource);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(value=RequestMappingPath.CreateDataByName,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponseEntity createDataByName(@PathVariable String name, @RequestPart("blob") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            dataService.createData(new DataEntity(name, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PutMapping(RequestMappingPath.UpdateDataByName)
    public MessageResponseEntity updateDataByName(@PathVariable String name, @RequestPart("blob") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            dataService.updateData(new DataEntity(name, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @DeleteMapping(RequestMappingPath.DeleteDataByName)
    public MessageResponseEntity deleteDataByName(@PathVariable String name) {
        try {
            dataService.deleteDataByName(name);
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
