package io.hychou.runnable.python.runner.entity;

import io.hychou.file.entity.FileInfo;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface TimeDependentFileInfo {
    @Value("#{target.crudTimeVariantDataStructure}")
    FileInfo getFileInfo();

    Date getRequiredTimestamp();
}
