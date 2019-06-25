package io.hychou.common.datastructure.blob.service;

import io.hychou.common.datastructure.blob.entity.BlobEntity;
import io.hychou.common.exception.service.ServiceException;

import java.util.List;

public interface BlobService<BLOB extends BlobEntity, INFO> {
    List<INFO> listInfo();

    INFO readInfoById(Long id) throws ServiceException;

    BLOB readById(Long id) throws ServiceException;

    List<INFO> readInfoByName(String name) throws ServiceException;

    List<BLOB> readByName(String name) throws ServiceException;

    BLOB create(BLOB blob) throws ServiceException;

    BLOB updateById(Long id, byte[] bytes) throws ServiceException;

    void deleteById(Long id) throws ServiceException;
}
