package io.hychou.file.service;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;

import java.util.List;

public interface FileService {
    List<FileInfo> listFileInfo();

    FileInfo readFileInfoByName(String name) throws ServiceException;

    FileEntity readFileByName(String name) throws ServiceException;

    FileEntity createFile(FileEntity fileEntity) throws ServiceException;

    FileEntity updateFile(FileEntity fileEntity) throws ServiceException;

    void deleteFileByName(String name) throws ServiceException;
}
