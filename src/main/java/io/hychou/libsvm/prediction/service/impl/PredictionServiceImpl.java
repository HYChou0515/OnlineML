package io.hychou.libsvm.prediction.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.client.ElementNotExistException;
import io.hychou.common.exception.service.client.NullParameterException;
import io.hychou.libsvm.prediction.dao.PredictionEntityRepository;
import io.hychou.libsvm.prediction.entity.PredictionEntity;
import io.hychou.libsvm.prediction.service.PredictionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PredictionServiceImpl implements PredictionService {

    private static final String ID_STRING = "Id";
    private final PredictionEntityRepository predictionEntityRepository;

    public PredictionServiceImpl(PredictionEntityRepository predictionEntityRepository) {
        this.predictionEntityRepository = predictionEntityRepository;
    }

    @Override
    public PredictionEntity readPredictionById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException(new PredictionEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<PredictionEntity> predictionEntity = predictionEntityRepository.findById(id);
        if (predictionEntity.isPresent()) {
            return predictionEntity.get();
        } else {
            throw new ElementNotExistException(new PredictionEntity().getStringNotExistForParam(ID_STRING, id));
        }
    }

    @Override
    public PredictionEntity createPrediction(PredictionEntity predictionEntity) throws ServiceException {
        if (predictionEntity == null || predictionEntity.getDataBytes() == null) {
            throw new NullParameterException(new PredictionEntity().getStringCreateNull());
        }
        predictionEntity = predictionEntityRepository.save(predictionEntity);
        return predictionEntity;
    }

    @Override
    public PredictionEntity updatePrediction(PredictionEntity predictionEntity) throws ServiceException {
        if (predictionEntity == null || predictionEntity.getId() == null || predictionEntity.getDataBytes() == null) {
            throw new NullParameterException(new PredictionEntity().getStringUpdateNull());
        }
        if (!predictionEntityRepository.existsById(predictionEntity.getId())) {
            throw new ElementNotExistException(new PredictionEntity().getStringNotExistForParam(ID_STRING, predictionEntity.getId()));
        }
        predictionEntity = predictionEntityRepository.save(predictionEntity);
        return predictionEntity;
    }

    @Override
    public void deletePredictionById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException("Trying to delete prediction with null id");
        }
        if (!predictionEntityRepository.existsById(id)) {
            throw new ElementNotExistException(new PredictionEntity().getStringQueryWithNullParam(ID_STRING));
        }
        predictionEntityRepository.deleteById(id);
    }
}
