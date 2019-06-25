package io.hychou.libsvm.model.dao;

import io.hychou.libsvm.model.entity.ModelEntity;
import org.springframework.data.repository.CrudRepository;

public interface ModelEntityRepository extends CrudRepository<ModelEntity, Long> {
}
