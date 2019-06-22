package io.hychou.file.dao;

import io.hychou.file.entity.FileEntity;
import io.hychou.file.entity.FileInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface FileEntityRepository extends CrudRepository<FileEntity, String> {
    List<FileInfo> findFileInfoBy();

    Optional<FileInfo> findFileInfoByName(String name);

    Optional<FileEntity> findByName(String name);

    boolean existsByName(String name);

    @Modifying
    @Transactional
    void deleteByName(String name);
}