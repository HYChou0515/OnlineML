package io.hychou.runnable.python.anacondayaml.entity;

import io.hychou.common.datastructure.blob.entity.BlobEntity;
import org.apache.commons.io.FileUtils;

import javax.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.hychou.common.Constant.EMPTY_STRING;

@Entity
public class AnacondaYamlEntity extends BlobEntity {

    public static AnacondaYamlEntity DEFAULT_ANACONDA_YAML_ENTITY = new AnacondaYamlEntity("default_anaconda_yaml", new byte[0]);

    public AnacondaYamlEntity(){
        super();
    }

    public AnacondaYamlEntity(String name, byte[] fileBytes) {
        super(name, fileBytes);
    }

    public void prepareEnvironment(Path directoryPath) throws IOException {
        if(this.getBlobBytes().length == 0) {
            return;
        }
        // TODO: change this into real environment preparation
        FileUtils.writeByteArrayToFile(new File(Paths.get(directoryPath.toString(), getName()).toString()), getBlobBytes());
    }
}
