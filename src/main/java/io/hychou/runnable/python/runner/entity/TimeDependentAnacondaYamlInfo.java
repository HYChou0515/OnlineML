package io.hychou.runnable.python.runner.entity;

import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface TimeDependentAnacondaYamlInfo {
    @Value("#{target.crudTimeVariantDataStructure}")
    AnacondaYamlInfo getAnacondaYamlInfo();

    Date getRequiredTimestamp();
}
