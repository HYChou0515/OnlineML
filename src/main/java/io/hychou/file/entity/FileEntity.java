package io.hychou.file.entity;

import io.hychou.common.datastructure.blob.entity.BlobEntity;
import org.apache.commons.io.FileUtils;

import javax.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Entity
public class FileEntity extends BlobEntity {

    public FileEntity() {
        super();
    }

    public FileEntity(String name, byte[] fileBytes) {
        super(name, fileBytes);
    }

    public void writeToFile(Path directoryPath) throws IOException {
        FileUtils.writeByteArrayToFile(new File(Paths.get(directoryPath.toString(), getName()).toString()), getBlobBytes());
    }

}