package io.hychou.file.service;

import io.hychou.common.datastructure.blob.service.BlobService;
import io.hychou.file.entity.FileEntity;

public interface FileService<INFO> extends BlobService<FileEntity, INFO> {
}
