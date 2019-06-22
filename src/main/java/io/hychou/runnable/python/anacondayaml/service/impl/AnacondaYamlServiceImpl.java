package io.hychou.runnable.python.anacondayaml.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementAlreadyExistException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.runnable.python.anacondayaml.dao.AnacondaYamlRepository;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import io.hychou.runnable.python.anacondayaml.service.AnacondaYamlService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnacondaYamlServiceImpl implements AnacondaYamlService {
    private static final String ID_STRING = "Name";
    private final AnacondaYamlRepository anacondaYamlRepository;

    public AnacondaYamlServiceImpl(AnacondaYamlRepository anacondaYamlRepository) {
        this.anacondaYamlRepository = anacondaYamlRepository;
    }

    @Override
    public List<AnacondaYamlInfo> listAnacondaYamlInfo() {
        return anacondaYamlRepository.findAnacondaYamlInfoBy();
    }

    @Override
    public AnacondaYamlInfo readAnacondaYamlInfoByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new AnacondaYamlEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<AnacondaYamlInfo> anacondaYamlInfo = anacondaYamlRepository.findAnacondaYamlInfoByName(name);
        if (anacondaYamlInfo.isPresent()) {
            return anacondaYamlInfo.get();
        } else {
            throw new ElementNotExistException(new AnacondaYamlEntity().getStringNotExistForParam(ID_STRING, name));
        }
    }

    @Override
    public AnacondaYamlEntity readAnacondaYamlByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new AnacondaYamlEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<AnacondaYamlEntity> anacondaYamlEntity = anacondaYamlRepository.findByName(name);
        if (anacondaYamlEntity.isPresent()) {
            return anacondaYamlEntity.get();
        } else {
            throw new ElementNotExistException(new AnacondaYamlEntity().getStringNotExistForParam(ID_STRING, name));
        }
    }

    @Override
    public AnacondaYamlEntity createAnacondaYaml(AnacondaYamlEntity anacondaYamlEntity) throws ServiceException {
        if (anacondaYamlEntity == null || anacondaYamlEntity.getName() == null || anacondaYamlEntity.getAnacondaYamlBytes() == null) {
            throw new NullParameterException(new AnacondaYamlEntity().getStringCreateNull());
        }
        if (anacondaYamlRepository.existsByName(anacondaYamlEntity.getName())) {
            throw new ElementAlreadyExistException(new AnacondaYamlEntity().getStringCreateExistingForParam(ID_STRING, anacondaYamlEntity.getName()));
        }
        checkIsValidAnacondaYaml(anacondaYamlEntity);
        anacondaYamlEntity = anacondaYamlRepository.save(anacondaYamlEntity);
        return anacondaYamlEntity;
    }

    @Override
    public AnacondaYamlEntity updateAnacondaYaml(AnacondaYamlEntity anacondaYamlEntity) throws ServiceException {
        if (anacondaYamlEntity == null || anacondaYamlEntity.getName() == null || anacondaYamlEntity.getAnacondaYamlBytes() == null) {
            throw new NullParameterException(new AnacondaYamlEntity().getStringUpdateNull());
        }
        if (!anacondaYamlRepository.existsByName(anacondaYamlEntity.getName())) {
            throw new ElementNotExistException(new AnacondaYamlEntity().getStringNotExistForParam(ID_STRING, anacondaYamlEntity.getName()));
        }
        checkIsValidAnacondaYaml(anacondaYamlEntity);
        anacondaYamlEntity = anacondaYamlRepository.save(anacondaYamlEntity);
        return anacondaYamlEntity;
    }

    @Override
    public void deleteAnacondaYamlByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new AnacondaYamlEntity().getStringDeleteWithNullParam(ID_STRING));
        }
        if (!anacondaYamlRepository.existsByName(name)) {
            throw new ElementNotExistException(new AnacondaYamlEntity().getStringNotExistForParam(ID_STRING, name));
        }
        anacondaYamlRepository.deleteByName(name);
    }

    //TODO: Use dry run to check the yaml is valid after the feature released in Anaconda 4.7
    private void checkIsValidAnacondaYaml(AnacondaYamlEntity anacondaYamlEntity) throws ServiceException {
//        try {
//
//        } catch (Exception e) {
//            throw new IllegalParameterException("Cannot read file or format not correct", e);
//        }
    }
}
