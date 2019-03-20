package io.hychou.libsvm.prediction.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.libsvm.prediction.dao.PredictionEntityRepository;
import io.hychou.libsvm.prediction.entity.PredictionEntity;
import io.hychou.libsvm.prediction.service.PredictionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class PredictionServiceImplTest {

    private PredictionEntityRepository predictionEntityRepository = Mockito.mock(PredictionEntityRepository.class);
    private PredictionService predictionService = new PredictionServiceImpl(predictionEntityRepository);

    private PredictionEntity a9aPrediction;
    private PredictionEntity a9aPredictionAnother;

    private PredictionEntity nullPrediction;
    private PredictionEntity nullIdPrediction;
    private PredictionEntity nullDataBytesPrediction;

    @Before
    public void setUp() {
        nullPrediction = null;
        nullIdPrediction = new PredictionEntity(null, "null id prediction".getBytes());
        nullDataBytesPrediction = new PredictionEntity(4L, null);

        a9aPrediction = new PredictionEntity();
        a9aPrediction.setId(1L);
        a9aPrediction.setDataBytes("This is a9a prediction".getBytes());

        a9aPredictionAnother = new PredictionEntity();
        a9aPredictionAnother.setId(3L);
        a9aPredictionAnother.setDataBytes("This is a9a prediction".getBytes());
    }

    // =====================================================================
    // readPredictionById
    // =====================================================================

    @Test
    public void readPredictionById_thenReturnPredictionEntity() throws ServiceException {
        // Arrange
        when(predictionEntityRepository.findById(a9aPrediction.getId())).thenReturn(Optional.of(a9aPrediction));

        // Act
        PredictionEntity found = predictionService.readPredictionById(a9aPrediction.getId());

        // Arrange
        assertEquals(a9aPrediction, found);
    }

    @Test
    public void readPredictionById_givenNullName_thenThrowNullParameterException() {
        // Assert
        // Act
        assertThrows(NullParameterException.class, () -> predictionService.readPredictionById(null));
    }

    @Test
    public void readPredictionById_givenNonExistName_thenThrowElementNotExistException() {
        // Arrange
        when(predictionEntityRepository.findById(a9aPrediction.getId())).thenReturn(Optional.empty());

        // Assert
        // Act
        assertThrows(ElementNotExistException.class, () -> predictionService.readPredictionById(a9aPrediction.getId()));
    }

    // =====================================================================
    // createPrediction
    // =====================================================================
    @Test
    public void createPrediction_thenReturnSavedDataEntity() throws ServiceException {
        // Arrange
        a9aPrediction.setId(null);
        doReturn(a9aPredictionAnother).when(predictionEntityRepository).save(a9aPrediction);

        // Act
        PredictionEntity found = predictionService.createPrediction(a9aPrediction);

        // Assert
        assertAll(
                () -> verify(predictionEntityRepository, times(1)).save(a9aPrediction),
                () -> assertEquals(a9aPredictionAnother, found)
        );
    }

    @Test
    public void createPrediction_givenNullDataEntity_thenThrowNullParameterException() {
        // Assert
        // Act
        assertAll(
                () -> assertThrows(NullParameterException.class, () -> predictionService.createPrediction(nullPrediction)),
                () -> assertThrows(NullParameterException.class, () -> predictionService.createPrediction(nullDataBytesPrediction))
        );
    }

    // =====================================================================
    // updatePrediction
    // =====================================================================

    @Test
    public void updatePrediction_thenReturnUpdatedDataEntity() throws ServiceException {
        // Arrange
        when(predictionEntityRepository.existsById(a9aPrediction.getId())).thenReturn(true);
        doReturn(a9aPredictionAnother).when(predictionEntityRepository).save(a9aPrediction);

        // Act
        PredictionEntity found = predictionService.updatePrediction(a9aPrediction);

        // Assert
        assertAll(
                () -> verify(predictionEntityRepository, times(1)).save(a9aPrediction),
                () -> assertEquals(a9aPredictionAnother, found)
        );
    }

    @Test
    public void updatePrediction_givenNullDataEntity_thenThrowNullParameterException() {
        // Assert
        // Act
        assertAll(
                () -> assertThrows(NullParameterException.class, () -> predictionService.updatePrediction(nullPrediction)),
                () -> assertThrows(NullParameterException.class, () -> predictionService.updatePrediction(nullIdPrediction)),
                () -> assertThrows(NullParameterException.class, () -> predictionService.updatePrediction(nullDataBytesPrediction))
        );
    }

    @Test
    public void updatePrediction_givenNonExistName_thenThrowElementNotExistException() {
        // Arrange
        when(predictionEntityRepository.existsById(a9aPrediction.getId())).thenReturn(false);

        // Assert
        // Act
        assertThrows(ElementNotExistException.class, () -> predictionService.updatePrediction(a9aPrediction));
    }

    // =====================================================================
    // deletePredictionById
    // =====================================================================

    @Test
    public void deletePredictionById_thenCallDeleteByNameOfDataEntityRepository() throws ServiceException {
        // Arrange
        when(predictionEntityRepository.existsById(a9aPrediction.getId())).thenReturn(true);
        doNothing().when(predictionEntityRepository).deleteById(a9aPrediction.getId());

        // Act
        predictionService.deletePredictionById(a9aPrediction.getId());

        // Assert
        verify(predictionEntityRepository, times(1)).deleteById(a9aPrediction.getId());
    }

    @Test
    public void deletePredictionById_givenNullName_thenThrowNullParameterException() {
        // Assert
        // Act
        assertThrows(NullParameterException.class, () -> predictionService.deletePredictionById(null));
    }

    @Test
    public void deletePredictionById_givenNonExistName_thenThrowElementNotExistException() {
        // Arrange
        when(predictionEntityRepository.existsById(a9aPrediction.getId())).thenReturn(false);
        doNothing().when(predictionEntityRepository).deleteById(a9aPrediction.getId());

        // Assert
        // Act
        assertThrows(ElementNotExistException.class, () -> predictionService.deletePredictionById(a9aPrediction.getId()));
    }
}
