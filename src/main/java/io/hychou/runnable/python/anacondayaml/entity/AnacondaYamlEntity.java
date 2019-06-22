package io.hychou.runnable.python.anacondayaml.entity;

import io.hychou.common.datastructure.blob.entity.BlobEntity;
import org.apache.commons.io.FileUtils;

import javax.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Entity
public class AnacondaYamlEntity extends BlobEntity {

    public AnacondaYamlEntity() {
        super();
    }

    public AnacondaYamlEntity(String name, byte[] fileBytes) {
        super(name, fileBytes);
    }

    public void prepareEnvironment(Path directoryPath) throws IOException {
        // TODO: change this into real environment preparation
        FileUtils.writeByteArrayToFile(new File(Paths.get(directoryPath.toString(), getName()).toString()), getBlobBytes());
    }
}
