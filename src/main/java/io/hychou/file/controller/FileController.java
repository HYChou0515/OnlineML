package io.hychou.file.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;
import io.hychou.file.service.FileService;
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
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(RequestMappingPath.ReadAllFileInfo)
    public MessageResponseEntity readAllFileInfo() {
        List<FileInfo> fileInfoList = fileService.listFileInfo();
        return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(fileInfoList);
    }

    @GetMapping(RequestMappingPath.ReadFileInfoByName)
    public MessageResponseEntity readFileInfoByName(@PathVariable String name) {
        try {
            FileInfo fileInfo = fileService.readFileInfoByName(name);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(fileInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadFileByName)
    public MessageResponseEntity readFileByName(@PathVariable String name) {
        try {
            FileEntity fileEntity = fileService.readFileByName(name);
            Resource resource = new ByteArrayResource(fileEntity.getFileBytes());
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).multipartFormData(fileEntity.getName(), resource);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(value = RequestMappingPath.CreateFileByName,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponseEntity createFileByName(@PathVariable String name, @RequestPart("blob") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            fileService.createFile(new FileEntity(name, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PutMapping(RequestMappingPath.UpdateFileByName)
    public MessageResponseEntity updateFileByName(@PathVariable String name, @RequestPart("blob") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            fileService.updateFile(new FileEntity(name, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @DeleteMapping(RequestMappingPath.DeleteFileByName)
    public MessageResponseEntity deleteFileByName(@PathVariable String name) {
        try {
            fileService.deleteFileByName(name);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
