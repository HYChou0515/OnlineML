package io.hychou.file.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementAlreadyExistException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.file.dao.FileEntityRepository;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;
import io.hychou.file.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {
    private static final String ID_STRING = "Name";
    private final FileEntityRepository fileEntityRepository;

    public FileServiceImpl(FileEntityRepository fileEntityRepository) {
        this.fileEntityRepository = fileEntityRepository;
    }

    @Override
    public List<FileInfo> listFileInfo() {
        return fileEntityRepository.findFileInfoBy();
    }

    @Override
    public FileInfo readFileInfoByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new FileEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<FileInfo> fileInfo = fileEntityRepository.findFileInfoByName(name);
        if (fileInfo.isPresent()) {
            return fileInfo.get();
        } else {
            throw new ElementNotExistException(new FileEntity().getStringNotExistForParam(ID_STRING, name));
        }
    }

    @Override
    public FileEntity readFileByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new FileEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<FileEntity> fileEntity = fileEntityRepository.findByName(name);
        if (fileEntity.isPresent()) {
            return fileEntity.get();
        } else {
            throw new ElementNotExistException(new FileEntity().getStringNotExistForParam(ID_STRING, name));
        }
    }

    @Override
    public FileEntity createFile(FileEntity fileEntity) throws ServiceException {
        if (fileEntity == null || fileEntity.getName() == null || fileEntity.getFileBytes() == null) {
            throw new NullParameterException(new FileEntity().getStringCreateNull());
        }
        if (fileEntityRepository.existsByName(fileEntity.getName())) {
            throw new ElementAlreadyExistException(new FileEntity().getStringCreateExistingForParam(ID_STRING, fileEntity.getName()));
        }
        fileEntity = fileEntityRepository.save(fileEntity);
        return fileEntity;
    }

    @Override
    public FileEntity updateFile(FileEntity fileEntity) throws ServiceException {
        if (fileEntity == null || fileEntity.getName() == null || fileEntity.getFileBytes() == null) {
            throw new NullParameterException(new FileEntity().getStringUpdateNull());
        }
        if (!fileEntityRepository.existsByName(fileEntity.getName())) {
            throw new ElementNotExistException(new FileEntity().getStringNotExistForParam(ID_STRING, fileEntity.getName()));
        }
        fileEntity = fileEntityRepository.save(fileEntity);
        return fileEntity;
    }

    @Override
    public void deleteFileByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new FileEntity().getStringDeleteWithNullParam(ID_STRING));
        }
        if (!fileEntityRepository.existsByName(name)) {
            throw new ElementNotExistException(new FileEntity().getStringNotExistForParam(ID_STRING, name));
        }
        fileEntityRepository.deleteByName(name);
    }
}
