package io.hychou.runnable.python.anacondayaml.service.impl;

import io.hychou.common.datastructure.blob.dao.BlobRepository;
import io.hychou.common.datastructure.blob.entity.BlobEntity;
import io.hychou.common.datastructure.blob.service.impl.BlobServiceImpl;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.client.NullParameterException;
import io.hychou.config.RunnablePathProperties;
import io.hychou.runnable.python.anacondayaml.AnacondaYamlService;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class AnacondaYamlServiceImpl extends BlobServiceImpl<AnacondaYamlEntity, AnacondaYamlInfo> implements AnacondaYamlService<AnacondaYamlInfo> {

    private final Logger logger = getLogger(this.getClass());
    private final Long defaultAnacondaYamlEntityId;

    public AnacondaYamlServiceImpl(BlobRepository<AnacondaYamlEntity, AnacondaYamlInfo> blobRepository,
                                   RunnablePathProperties runnablePathProperties) {
        super(blobRepository);
        AnacondaYamlEntity defaultAnacondaYamlEntity = new AnacondaYamlEntity("default_anaconda_yaml", new byte[0], runnablePathProperties.getAnacondaEnvBaseDir());
        Long _defaultAnacondaYamlEntityId;
        try {
            _defaultAnacondaYamlEntityId = create(defaultAnacondaYamlEntity).getId();
        } catch (ServiceException e) {
            _defaultAnacondaYamlEntityId = null;
            logger.error(e.getMessage());
        }
        defaultAnacondaYamlEntityId = _defaultAnacondaYamlEntityId;
    }

    @Override
    public AnacondaYamlInfo readInfoById(Long id) throws ServiceException {
        if (id == null) {
            return super.readInfoById(defaultAnacondaYamlEntityId);
        }
        return super.readInfoById(id);
    }

    @Override
    public AnacondaYamlEntity readById(Long id) throws ServiceException {
        if (id == null) {
            return super.readById(defaultAnacondaYamlEntityId);
        }
        return super.readById(id);
    }

    @Override
    public AnacondaYamlEntity create(AnacondaYamlEntity anacondaYamlEntity) throws ServiceException {
        if (anacondaYamlEntity == null || anacondaYamlEntity.getBlobBytes() == null) {
            throw new NullParameterException(new BlobEntity().getStringCreateNull());
        }
        checkIsValidAnacondaYaml(anacondaYamlEntity.getBlobBytes());
        return super.create(anacondaYamlEntity);
    }

    @Override
    public AnacondaYamlEntity updateById(Long id, byte[] bytes) throws ServiceException {
        if (bytes == null) {
            throw new NullParameterException(new BlobEntity().getStringUpdateNull());
        }
        checkIsValidAnacondaYaml(bytes);
        return super.updateById(id, bytes);
    }

    private void checkIsValidAnacondaYaml(byte[] bytes) throws ServiceException {
//        try {
//
//        } catch (Exception e) {
//            throw new IllegalParameterException("Cannot read file or format not correct", e);
//        }
    }
}
