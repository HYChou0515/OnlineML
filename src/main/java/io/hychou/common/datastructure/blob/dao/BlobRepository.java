package io.hychou.common.datastructure.blob.dao;

import io.hychou.common.datastructure.blob.entity.BlobEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BlobRepository<BLOB extends BlobEntity, INFO> extends CrudRepository<BLOB, Long> {
    List<INFO> findBlobInfoBy();

    Optional<INFO> findBlobInfoById(Long id);

    List<INFO> findBlobInfoByName(String name);

    List<BLOB> findByName(String name);

    @Modifying
    @Transactional
    void deleteById(Long id);
}
