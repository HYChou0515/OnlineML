package io.hychou.file.dao;

import io.hychou.common.datastructure.blob.dao.BlobRepository;
import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;


public interface FileEntityRepository extends BlobRepository<FileEntity, FileInfo> {
}