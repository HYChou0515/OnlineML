package io.hychou.common.datastructure.blob.service.impl;

import io.hychou.common.datastructure.blob.dao.BlobRepository;
import io.hychou.common.datastructure.blob.entity.BlobEntity;
import io.hychou.common.datastructure.blob.service.BlobService;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.IllegalParameterException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

public class BlobServiceImpl<BLOB extends BlobEntity, INFO> implements BlobService<BLOB, INFO> {
    protected static final String ID_STRING = "id";
    protected static final String NAME_STRING = "name";
    protected static final String CREATE_EMPTY_NAME="Trying to create blob with empty name";

    @Getter
    protected final BlobRepository<BLOB, INFO> blobRepository;

    public BlobServiceImpl(BlobRepository<BLOB, INFO> blobRepository) {
        this.blobRepository = blobRepository;
    }

    @Override
    public List<INFO> listBlobInfo() {
        return blobRepository.findBlobInfoBy();
    }

    @Override
    public INFO readBlobInfoById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new BlobEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<INFO> blobInfo = blobRepository.findBlobInfoById(id);
        if (blobInfo.isPresent()) {
            return blobInfo.get();
        } else {
            throw new ElementNotExistException(new BlobEntity().getStringNotExistForParam(ID_STRING, id));
        }
    }

    @Override
    public BLOB readBlobById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new BlobEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<BLOB> blob = blobRepository.findById(id);
        if (blob.isPresent()) {
            return blob.get();
        } else {
            throw new ElementNotExistException(new BlobEntity().getStringNotExistForParam(ID_STRING, id));
        }
    }

    @Override
    public List<INFO> readBlobInfoByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new BlobEntity().getStringQueryWithNullParam(NAME_STRING));
        }
        return blobRepository.findBlobInfoByName(name);
    }

    @Override
    public List<BLOB> readBlobByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new BlobEntity().getStringQueryWithNullParam(NAME_STRING));
        }
        return blobRepository.findByName(name);
    }

    @Override
    public BLOB createBlob(BLOB blob) throws ServiceException {
        if (blob == null || blob.getName() == null || blob.getBlobBytes() == null) {
            throw new NullParameterException(new BlobEntity().getStringCreateNull());
        }
        if (blob.getName().isEmpty()) {
            throw new IllegalParameterException(CREATE_EMPTY_NAME);
        }
        blob = blobRepository.save(blob);
        return blob;
    }

    @Override
    public BLOB updateBlobById(Long id, byte[] bytes) throws ServiceException {
        if (id == null || bytes == null) {
            throw new NullParameterException(new BlobEntity().getStringUpdateNull());
        }
        BLOB blob = readBlobById(id);
        blob.setBlobBytes(bytes);
        blob = blobRepository.save(blob);
        return blob;
    }

    @Override
    public void deleteBlobById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new BlobEntity().getStringDeleteWithNullParam(ID_STRING));
        }
        if (!blobRepository.existsById(id)) {
            throw new ElementNotExistException(new BlobEntity().getStringNotExistForParam(ID_STRING, id));
        }
        blobRepository.deleteById(id);
    }
}
