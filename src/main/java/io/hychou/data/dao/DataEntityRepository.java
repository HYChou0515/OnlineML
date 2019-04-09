package io.hychou.data.dao;

import io.hychou.data.entity.DataEntity;
import io.hychou.data.entity.DataInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface DataEntityRepository extends CrudRepository<DataEntity, String> {
    List<DataInfo> findDataInfoBy();
    Optional<DataEntity> findByName(String name);
    boolean existsByName(String name);
    @Modifying
    @Transactional
    void deleteByName(String name);
}