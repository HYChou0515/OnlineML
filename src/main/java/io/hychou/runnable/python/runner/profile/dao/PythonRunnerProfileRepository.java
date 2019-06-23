package io.hychou.runnable.python.runner.profile.dao;

import io.hychou.file.entity.FileEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileEntity;
import io.hychou.runnable.python.runner.profile.entity.PythonRunnerProfileInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PythonRunnerProfileRepository extends CrudRepository<PythonRunnerProfileEntity, Long> {
    List<PythonRunnerProfileInfo> findPythonRunnerProfileInfoBy();

    Optional<PythonRunnerProfileInfo> findPythonRunnerProfileInfoById(Long id);

    List<PythonRunnerProfileEntity> findByPythonCode_TimeVariantDataAndEnvironment_TimeVariantDataAndDependencies_TimeVariantData(
            FileEntity pythonCode, AnacondaYamlEntity environment, Set<FileEntity> dependencies);
}
