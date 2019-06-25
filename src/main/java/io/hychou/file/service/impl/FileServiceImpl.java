package io.hychou.file.service.impl;

import io.hychou.common.datastructure.blob.dao.BlobRepository;
import io.hychou.common.datastructure.blob.service.impl.BlobServiceImpl;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;
import io.hychou.file.service.FileService;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl extends BlobServiceImpl<FileEntity, FileInfo> implements FileService<FileInfo> {
    public FileServiceImpl(BlobRepository<FileEntity, FileInfo> blobRepository) {
        super(blobRepository);
    }
}
