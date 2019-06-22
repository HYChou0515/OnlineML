package io.hychou.runnable.python.anacondayaml.entity;

import io.hychou.common.Constant;
import io.hychou.common.SignificantField;
import io.hychou.common.datastructure.AbstractCrudTimeVariantDataStructure;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class AnacondaYamlEntity extends AbstractCrudTimeVariantDataStructure {
    @Id
    @Getter
    @Setter
    private String name;

    @Column(length = Constant.GB)
    @Getter
    @Setter
    private byte[] anacondaYamlBytes;

    public AnacondaYamlEntity() {
    }

    public AnacondaYamlEntity(String name, byte[] anacondaYamlBytes) {
        this.name = name;
        this.anacondaYamlBytes = anacondaYamlBytes;
    }

    @PrePersist
    @PreUpdate
    public void updateTimeStamps() {
        this.lastModified = new Date();
    }

    public void prepareEnvironment(Path directoryPath) throws IOException {
        // TODO: change this into real environment preparation
        FileUtils.writeByteArrayToFile(new File(Paths.get(directoryPath.toString(), getName()).toString()), getAnacondaYamlBytes());
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("name", this.name));
        fields.add(new SignificantField("lastModified", this.lastModified));
        return fields;
    }
}
