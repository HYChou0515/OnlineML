package io.hychou.common.datastructure.blob.service;

import io.hychou.common.datastructure.blob.entity.BlobEntity;
import io.hychou.common.exception.service.ServiceException;

import java.util.List;

public interface BlobService<BLOB extends BlobEntity, INFO> {
    List<INFO> listBlobInfo();

    INFO readBlobInfoById(Long id) throws ServiceException;

    BLOB readBlobById(Long id) throws ServiceException;

    List<INFO> readBlobInfoByName(String name) throws ServiceException;

    List<BLOB> readBlobByName(String name) throws ServiceException;

    BLOB createBlob(BLOB blob) throws ServiceException;

    BLOB updateBlobById(Long id, byte[] bytes) throws ServiceException;

    void deleteBlobById(Long id) throws ServiceException;
}
