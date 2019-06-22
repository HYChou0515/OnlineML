package io.hychou.runnable.python.runner.dao;

import io.hychou.file.entity.FileEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.runner.entity.PythonRunnerEntity;
import io.hychou.runnable.python.runner.entity.PythonRunnerInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PythonRunnerRepository extends CrudRepository<PythonRunnerEntity, Long> {
    List<PythonRunnerInfo> findPythonRunnerInfoBy();

    Optional<PythonRunnerInfo> findPythonRunnerInfoById(Long id);

    List<PythonRunnerEntity> findByPythonCode_TimeVariantDataAndEnvironment_TimeVariantData(
            FileEntity pythonCode, AnacondaYamlEntity environment);
}
