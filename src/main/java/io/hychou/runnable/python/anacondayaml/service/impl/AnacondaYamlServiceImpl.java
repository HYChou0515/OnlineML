package io.hychou.runnable.python.anacondayaml.service.impl;

import io.hychou.common.datastructure.blob.dao.BlobRepository;
import io.hychou.common.datastructure.blob.entity.BlobEntity;
import io.hychou.common.datastructure.blob.service.impl.BlobServiceImpl;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.client.NullParameterException;
import io.hychou.config.RunnablePathProperties;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class AnacondaYamlServiceImpl extends BlobServiceImpl<AnacondaYamlEntity, AnacondaYamlInfo> {

    private final Logger logger = getLogger(this.getClass());
    private final Long defaultAnacondaYamlEntityId;

    public AnacondaYamlServiceImpl(BlobRepository<AnacondaYamlEntity, AnacondaYamlInfo> blobRepository,
                                   RunnablePathProperties runnablePathProperties) {
        super(blobRepository);
        AnacondaYamlEntity defaultAnacondaYamlEntity = new AnacondaYamlEntity("default_anaconda_yaml", new byte[0], runnablePathProperties.getAnacondaEnvBaseDir());
        Long _defaultAnacondaYamlEntityId;
        try {
            _defaultAnacondaYamlEntityId = createBlob(defaultAnacondaYamlEntity).getId();
        } catch (ServiceException e) {
            _defaultAnacondaYamlEntityId = null;
            logger.error(e.getMessage());
        }
        defaultAnacondaYamlEntityId = _defaultAnacondaYamlEntityId;
    }

    @Override
    public AnacondaYamlInfo readBlobInfoById(Long id) throws ServiceException {
        if (id == null) {
            return super.readBlobInfoById(defaultAnacondaYamlEntityId);
        }
        return super.readBlobInfoById(id);
    }

    @Override
    public AnacondaYamlEntity readBlobById(Long id) throws ServiceException {
        if (id == null) {
            return super.readBlobById(defaultAnacondaYamlEntityId);
        }
        return super.readBlobById(id);
    }

    @Override
    public AnacondaYamlEntity createBlob(AnacondaYamlEntity anacondaYamlEntity) throws ServiceException {
        if (anacondaYamlEntity == null || anacondaYamlEntity.getBlobBytes() == null) {
            throw new NullParameterException(new BlobEntity().getStringCreateNull());
        }
        checkIsValidAnacondaYaml(anacondaYamlEntity.getBlobBytes());
        return super.createBlob(anacondaYamlEntity);
    }

    @Override
    public AnacondaYamlEntity updateBlobById(Long id, byte[] bytes) throws ServiceException {
        if (bytes == null) {
            throw new NullParameterException(new BlobEntity().getStringUpdateNull());
        }
        checkIsValidAnacondaYaml(bytes);
        return super.updateBlobById(id, bytes);
    }

    private void checkIsValidAnacondaYaml(byte[] bytes) throws ServiceException {
//        try {
//
//        } catch (Exception e) {
//            throw new IllegalParameterException("Cannot read file or format not correct", e);
//        }
    }
}
