package io.hychou.file.service.impl;

import io.hychou.common.datastructure.blob.dao.BlobRepository;
import io.hychou.common.datastructure.blob.service.impl.BlobServiceImpl;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl extends BlobServiceImpl<FileEntity, FileInfo> {
    public FileServiceImpl(BlobRepository<FileEntity, FileInfo> blobRepository) {
        super(blobRepository);
    }
}
