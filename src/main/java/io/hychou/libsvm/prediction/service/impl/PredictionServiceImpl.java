package io.hychou.libsvm.prediction.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.libsvm.prediction.dao.PredictionEntityRepository;
import io.hychou.libsvm.prediction.entity.PredictionEntity;
import io.hychou.libsvm.prediction.service.PredictionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PredictionServiceImpl implements PredictionService {

    private final PredictionEntityRepository predictionEntityRepository;

    public PredictionServiceImpl(PredictionEntityRepository predictionEntityRepository) {
        this.predictionEntityRepository = predictionEntityRepository;
    }

    private static String predictionWithIdDoesNotExist(Long id) {
        return "Prediction with id=" + id + " does not exist";
    }

    @Override
    public PredictionEntity readPredictionById(Long id) throws ServiceException {
        if (id == null) {
            throw new NullParameterException("Trying to query with null id");
        }
        Optional<PredictionEntity> predictionEntity = predictionEntityRepository.findById(id);
        if (predictionEntity.isPresent()) {
            return predictionEntity.get();
        } else {
            throw new ElementNotExistException(predictionWithIdDoesNotExist(id));
        }
    }

    @Override
    public PredictionEntity createPrediction(PredictionEntity predictionEntity) throws ServiceException {
        if (predictionEntity == null || predictionEntity.getDataBytes() == null) {
            throw new NullParameterException("Trying to create null prediction");
        }
        predictionEntity = predictionEntityRepository.save(predictionEntity);
        return predictionEntity;
    }

    @Override
    public PredictionEntity updatePrediction(PredictionEntity predictionEntity) throws ServiceException {
        if (predictionEntity == null || predictionEntity.getId() == null || predictionEntity.getDataBytes() == null) {
            throw new NullParameterException("Trying to update null prediction");
        }
        if (!predictionEntityRepository.existsById(predictionEntity.getId())) {
            throw new ElementNotExistException(predictionWithIdDoesNotExist(predictionEntity.getId()));
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
            throw new ElementNotExistException(predictionWithIdDoesNotExist(id));
        }
        predictionEntityRepository.deleteById(id);
    }
}
