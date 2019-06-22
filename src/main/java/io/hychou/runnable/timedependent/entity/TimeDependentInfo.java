package io.hychou.runnable.timedependent.entity;

import io.hychou.file.entity.FileInfo;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface TimeDependentInfo {
    @Value("#{target.timeVariantData}")
    FileInfo getFileInfo();

    Date getRequiredTimestamp();
}
