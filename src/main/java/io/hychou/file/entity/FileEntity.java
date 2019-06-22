package io.hychou.file.entity;

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
public class FileEntity extends AbstractCrudTimeVariantDataStructure {
    @Id
    @Getter
    @Setter
    private String name;

    @Column(length = Constant.GB)
    @Getter
    @Setter
    private byte[] fileBytes;

    public FileEntity() {
    }

    public FileEntity(String name, byte[] fileBytes) {
        this.name = name;
        this.fileBytes = fileBytes;
    }

    @PrePersist
    @PreUpdate
    public void updateTimeStamps() {
        this.lastModified = new Date();
    }

    public void writeToFile(Path directoryPath) throws IOException {
        FileUtils.writeByteArrayToFile(new File(Paths.get(directoryPath.toString(), getName()).toString()), getFileBytes());
    }

    @Override
    public List<SignificantField> significantFields() {
        List<SignificantField> fields = new ArrayList<>();
        fields.add(new SignificantField("name", this.name));
        fields.add(new SignificantField("lastModified", this.lastModified));
        return fields;
    }
}