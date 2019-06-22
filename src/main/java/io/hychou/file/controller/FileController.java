package io.hychou.file.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.datastructure.blob.service.BlobService;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;
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

    private final BlobService<FileEntity, FileInfo> fileService;

    @Autowired
    public FileController(BlobService<FileEntity, FileInfo> fileService) {
        this.fileService = fileService;
    }

    @GetMapping(RequestMappingPath.ReadAllFileInfo)
    public MessageResponseEntity readAllFileInfo(@RequestParam(value = "name", required = false, defaultValue = "") String name) {
        if (name.isEmpty()) {
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(fileService.listBlobInfo());
        }
        try {
            List<FileInfo> fileInfo = fileService.readBlobInfoByName(name);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(fileInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadFileInfoById)
    public MessageResponseEntity readFileInfoById(@PathVariable Long id) {
        try {
            FileInfo fileInfo = fileService.readBlobInfoById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(fileInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadFileById)
    public MessageResponseEntity readFileById(@PathVariable Long id) {
        try {
            FileEntity fileEntity = fileService.readBlobById(id);
            Resource resource = new ByteArrayResource(fileEntity.getBlobBytes());
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).multipartFormData(fileEntity.getName(), resource);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(value = RequestMappingPath.CreateFileByName,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponseEntity createFileByName(@PathVariable String name, @RequestPart("file") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            fileService.createBlob(new FileEntity(name, bytes));
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PutMapping(RequestMappingPath.UpdateFileById)
    public MessageResponseEntity updateFileById(@PathVariable Long id, @RequestPart("file") MultipartFile multipartFile) {
        try {
            byte[] bytes = getBytesFrom(multipartFile);
            fileService.updateBlobById(id, bytes);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @DeleteMapping(RequestMappingPath.DeleteFileById)
    public MessageResponseEntity deleteFileById(@PathVariable Long id) {
        try {
            fileService.deleteBlobById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).build();
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
