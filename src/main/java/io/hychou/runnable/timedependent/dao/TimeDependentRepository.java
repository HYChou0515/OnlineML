package io.hychou.runnable.timedependent.dao;

import io.hychou.common.datastructure.blob.entity.BlobEntity;
import io.hychou.runnable.timedependent.entity.TimeDependentEntity;
import org.springframework.data.repository.CrudRepository;

public interface TimeDependentRepository<E extends BlobEntity> extends CrudRepository<TimeDependentEntity<E>, Long> {
}
