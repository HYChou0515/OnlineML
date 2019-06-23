package io.hychou.runnable.python.runner.profile.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.datastructure.blob.service.BlobService;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileEntity;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileInfo;
import io.hychou.runnable.python.runner.profile.service.PythonRunnerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static io.hychou.common.Constant.*;

@RestController
public class PythonRunnerProfileController {

    private final PythonRunnerProfileService pythonRunnerProfileService;
    private final BlobService<FileEntity, FileInfo> fileService;
    private final BlobService<AnacondaYamlEntity, AnacondaYamlInfo> anacondaYamlService;

    @Autowired
    public PythonRunnerProfileController(PythonRunnerProfileService pythonRunnerProfileService, BlobService<FileEntity, FileInfo> fileService, BlobService<AnacondaYamlEntity, AnacondaYamlInfo> anacondaYamlService) {
        this.pythonRunnerProfileService = pythonRunnerProfileService;
        this.fileService = fileService;
        this.anacondaYamlService = anacondaYamlService;
    }

    @GetMapping(RequestMappingPath.ReadPythonRunnerProfileInfo)
    public MessageResponseEntity readPythonRunnerProfileInfo() {
        List<PythonRunnerProfileInfo> pythonRunnerProfileInfoList = pythonRunnerProfileService.listPythonRunnerProfileInfo();
        return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(pythonRunnerProfileInfoList);
    }

    @GetMapping(RequestMappingPath.ReadPythonRunnerProfileInfoById)
    public MessageResponseEntity readPythonRunnerProfileInfoById(@PathVariable Long id) {
        try {
            PythonRunnerProfileInfo pythonRunnerProfileInfo = pythonRunnerProfileService.readPythonRunnerProfileInfoById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(pythonRunnerProfileInfo);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @GetMapping(RequestMappingPath.ReadPythonRunnerProfileById)
    public MessageResponseEntity readPythonRunnerProfileById(@PathVariable Long id) {
        try {
            PythonRunnerProfileEntity pythonRunnerProfileEntity = pythonRunnerProfileService.readPythonRunnerProfileById(id);
            return MessageResponseEntity.ok(SUCCESS_MESSAGE).body(pythonRunnerProfileEntity);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }

    @PostMapping(RequestMappingPath.CreatePythonRunnerProfile)
    public MessageResponseEntity createPythonRunnerProfile(@PathVariable Long pythonCodeId,
                                                           @RequestParam(required = false, defaultValue = EMPTY_STRING) Long environmentId,
                                                           @RequestParam(value = "dependenciesIds[]", required = false, defaultValue = EMPTY_STRING) Long[] dependenciesIds) {
        try {
            FileEntity pythonCode = fileService.readBlobById(pythonCodeId);
            AnacondaYamlEntity environment = anacondaYamlService.readBlobById(environmentId);
            List<FileEntity> dependencies = new ArrayList<>();
            for (Long dependencyId : dependenciesIds) {
                dependencies.add(fileService.readBlobById(dependencyId));
            }
            PythonRunnerProfileEntity pythonRunnerProfileEntity = new PythonRunnerProfileEntity(pythonCode, dependencies, environment);
            pythonRunnerProfileEntity = pythonRunnerProfileService.createPythonRunnerProfile(pythonRunnerProfileEntity);
            return MessageResponseEntity.ok(pythonRunnerProfileEntity.getId(), SUCCESS_MESSAGE);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
