package io.hychou.runnable.python.runner.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.datastructure.blob.service.BlobService;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import io.hychou.runnable.python.runner.entity.PythonRunnerEntity;
import io.hychou.runnable.python.runner.entity.PythonRunnerInfo;
import io.hychou.runnable.python.runner.service.PythonRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static io.hychou.common.Constant.SUCCESS_MESSAGE;

@RestController
public class PythonRunnerController {

    private final PythonRunnerService pythonRunnerService;
    private final BlobService<FileEntity, FileInfo> fileService;
    private final BlobService<AnacondaYamlEntity, AnacondaYamlInfo> anacondaYamlService;

    @Autowired
    public PythonRunnerController(PythonRunnerService pythonRunnerService, BlobService<FileEntity, FileInfo> fileService, BlobService<AnacondaYamlEntity, AnacondaYamlInfo> anacondaYamlService) {
        this.pythonRunnerService = pythonRunnerService;
        this.fileService = fileService;
        this.anacondaYamlService = anacondaYamlService;
    }

    @GetMapping(RequestMappingPath.ReadPythonRunnerInfo)
    public MessageResponseEntity readPythonRunnerInfo() {
        List<PythonRunnerInfo> pythonRunnerInfoList = pythonRunnerService.listPythonRunnerInfo();
        return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(pythonRunnerInfoList);
    }

    @GetMapping(RequestMappingPath.ReadPythonRunnerInfoById)
    public MessageResponseEntity readPythonRunnerInfoById(@PathVariable Long id) {
        try {
            PythonRunnerInfo pythonRunnerInfo = pythonRunnerService.readPythonRunnerInfoById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(pythonRunnerInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadPythonRunnerById)
    public MessageResponseEntity readPythonRunnerById(@PathVariable Long id) {
        try {
            PythonRunnerEntity pythonRunnerEntity = pythonRunnerService.readPythonRunnerById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(pythonRunnerEntity);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(RequestMappingPath.CreatePythonRunner)
    public MessageResponseEntity createPythonRunner(@PathVariable Long pythonCodeId,
                                                    @PathVariable Long environmentId,
                                                    @RequestParam(value = "dependenciesIds[]", required = false, defaultValue = "") Long[] dependenciesIds) {
        try {
            FileEntity pythonCode = fileService.readBlobById(pythonCodeId);
            AnacondaYamlEntity environment = anacondaYamlService.readBlobById(environmentId);
            List<FileEntity> dependencies = new ArrayList<>();
            for (Long dependencyId : dependenciesIds) {
                dependencies.add(fileService.readBlobById(dependencyId));
            }
            PythonRunnerEntity pythonRunnerEntity = new PythonRunnerEntity(pythonCode, dependencies, environment);
            pythonRunnerEntity = pythonRunnerService.createPythonRunner(pythonRunnerEntity);
            return MessageResponseEntity.ok(pythonRunnerEntity.getId(), SUCCESS_MESSAGE);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
