package io.hychou.runnable.python.runner.profile.controller;

import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;
import io.hychou.file.service.FileService;
import io.hychou.runnable.python.anacondayaml.AnacondaYamlService;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileEntity;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileInfo;
import io.hychou.runnable.python.runner.profile.service.PythonRunnerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static io.hychou.common.Constant.EMPTY_STRING;
import static io.hychou.common.Constant.SUCCESS_MESSAGE;

@RestController
public class PythonRunnerProfileController {

    private final PythonRunnerProfileService pythonRunnerProfileService;
    private final FileService<FileInfo> fileService;
    private final AnacondaYamlService<AnacondaYamlInfo> anacondaYamlService;

    @Autowired
    public PythonRunnerProfileController(PythonRunnerProfileService pythonRunnerProfileService,
                                         FileService<FileInfo> fileService,
                                         AnacondaYamlService<AnacondaYamlInfo> anacondaYamlService) {
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
            FileEntity pythonCode = fileService.readById(pythonCodeId);
            AnacondaYamlEntity environment = anacondaYamlService.readById(environmentId);
            List<FileEntity> dependencies = new ArrayList<>();
            for (Long dependencyId : dependenciesIds) {
                dependencies.add(fileService.readById(dependencyId));
            }
            PythonRunnerProfileEntity pythonRunnerProfileEntity = new PythonRunnerProfileEntity(pythonCode, dependencies, environment);
            pythonRunnerProfileEntity = pythonRunnerProfileService.createPythonRunnerProfile(pythonRunnerProfileEntity);
            PythonRunnerProfileInfo pythonRunnerProfileInfo = pythonRunnerProfileService.readPythonRunnerProfileInfoById(pythonRunnerProfileEntity.getId());
            return MessageResponseEntity.ok(pythonRunnerProfileInfo, SUCCESS_MESSAGE);
        } catch (ServiceException e) {
            return e.getMessageResponseEntity();
        }
    }
}
