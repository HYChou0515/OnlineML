package io.hychou.libsvm.prediction.dao;

import io.hychou.libsvm.prediction.entity.PredictionEntity;
import org.springframework.data.repository.CrudRepository;

public interface PredictionEntityRepository extends CrudRepository<PredictionEntity, Long> {
}
