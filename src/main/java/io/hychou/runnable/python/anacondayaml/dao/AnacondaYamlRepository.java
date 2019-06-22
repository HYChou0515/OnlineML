package io.hychou.runnable.python.anacondayaml.dao;

import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlEntity;
import io.hychou.runnable.python.anacondayaml.entity.AnacondaYamlInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AnacondaYamlRepository extends CrudRepository<AnacondaYamlEntity, String> {
    List<AnacondaYamlInfo> findAnacondaYamlInfoBy();

    Optional<AnacondaYamlInfo> findAnacondaYamlInfoByName(String name);

    Optional<AnacondaYamlEntity> findByName(String name);

    boolean existsByName(String name);

    @Modifying
    @Transactional
    void deleteByName(String name);
}
