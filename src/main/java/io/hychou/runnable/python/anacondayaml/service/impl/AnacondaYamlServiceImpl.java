package io.hychou.runnable.python.anacondayaml.service.impl;

import io.hychou.common.datastructure.blob.dao.BlobRepository;
import io.hychou.common.datastructure.blob.entity.BlobEntity;
import io.hychou.common.datastructure.blob.service.impl.BlobServiceImpl;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import org.springframework.stereotype.Service;

@Service
public class AnacondaYamlServiceImpl extends BlobServiceImpl<AnacondaYamlEntity, AnacondaYamlInfo> {
    public AnacondaYamlServiceImpl(BlobRepository<AnacondaYamlEntity, AnacondaYamlInfo> blobRepository) {
        super(blobRepository);
    }

    @Override
    public AnacondaYamlEntity createBlob(AnacondaYamlEntity anacondaYamlEntity) throws ServiceException {
        if (anacondaYamlEntity == null || anacondaYamlEntity.getName() == null || anacondaYamlEntity.getBlobBytes() == null) {
            throw new NullParameterException(new BlobEntity().getStringCreateNull());
        }
        checkIsValidAnacondaYaml(anacondaYamlEntity.getBlobBytes());
        anacondaYamlEntity = getBlobRepository().save(anacondaYamlEntity);
        return anacondaYamlEntity;
    }

    @Override
    public AnacondaYamlEntity updateBlobById(Long id, byte[] bytes) throws ServiceException {
        if (id == null || bytes == null) {
            throw new NullParameterException(new BlobEntity().getStringUpdateNull());
        }
        checkIsValidAnacondaYaml(bytes);
        AnacondaYamlEntity anacondaYamlEntity = readBlobById(id);
        anacondaYamlEntity.setBlobBytes(bytes);
        anacondaYamlEntity = getBlobRepository().save(anacondaYamlEntity);
        return anacondaYamlEntity;
    }

    //TODO: Use dry run to check the yaml is valid after the feature released in Anaconda 4.7
    private void checkIsValidAnacondaYaml(byte[] bytes) throws ServiceException {
//        try {
//
//        } catch (Exception e) {
//            throw new IllegalParameterException("Cannot read file or format not correct", e);
//        }
    }
}
