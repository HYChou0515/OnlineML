package io.hychou.libsvm.model.service.impl;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.client.ElementNotExistException;
import io.hychou.common.exception.service.client.NullParameterException;
import io.hychou.libsvm.model.dao.ModelEntityRepository;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.model.service.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class ModelServiceImplTest {

    private ModelEntityRepository modelEntityRepository = Mockito.mock(ModelEntityRepository.class);
    private ModelService modelService = new ModelServiceImpl(modelEntityRepository);

    private ModelEntity a9aModel;
    private ModelEntity a9aModelAnother;

    private ModelEntity nullModel;
    private ModelEntity nullIdModel;
    private ModelEntity nullDataBytesModel;

    @BeforeEach
    public void setUp() {
        nullModel = null;
        nullIdModel = new ModelEntity(null, "null id model".getBytes());
        nullDataBytesModel = new ModelEntity(4L, null);

        a9aModel = new ModelEntity();
        a9aModel.setId(1L);
        a9aModel.setDataBytes("This is a9a model".getBytes());

        a9aModelAnother = new ModelEntity();
        a9aModelAnother.setId(3L);
        a9aModelAnother.setDataBytes("This is a9a model".getBytes());
    }

    // =====================================================================
    // readModelById
    // =====================================================================

    @Test
    public void readModelById_thenReturnModelEntity() throws ServiceException {
        // Arrange
        when(modelEntityRepository.findById(a9aModel.getId())).thenReturn(Optional.of(a9aModel));

        // Act
        ModelEntity found = modelService.readModelById(a9aModel.getId());

        // Arrange
        assertEquals(a9aModel, found);
    }

    @Test
    public void readModelById_givenNullName_thenThrowNullParameterException() {
        // Assert
        // Act
        assertThrows(NullParameterException.class, () -> modelService.readModelById(null));
    }

    @Test
    public void readModelById_givenNonExistName_thenThrowElementNotExistException() {
        // Arrange
        when(modelEntityRepository.findById(a9aModel.getId())).thenReturn(Optional.empty());

        // Assert
        // Act
        assertThrows(ElementNotExistException.class, () -> modelService.readModelById(a9aModel.getId()));
    }

    // =====================================================================
    // createModel
    // =====================================================================
    @Test
    public void createModel_thenReturnSavedDataEntity() throws ServiceException {
        // Arrange
        a9aModel.setId(null);
        doReturn(a9aModelAnother).when(modelEntityRepository).save(a9aModel);

        // Act
        ModelEntity found = modelService.createModel(a9aModel);

        // Assert
        assertAll(
                () -> verify(modelEntityRepository, times(1)).save(a9aModel),
                () -> assertEquals(a9aModelAnother, found)
        );
    }

    @Test
    public void createModel_givenNullDataEntity_thenThrowNullParameterException() {
        // Assert
        // Act
        assertAll(
                () -> assertThrows(NullParameterException.class, () -> modelService.createModel(nullModel)),
                () -> assertThrows(NullParameterException.class, () -> modelService.createModel(nullDataBytesModel))
        );
    }

    // =====================================================================
    // updateModel
    // =====================================================================

    @Test
    public void updateModel_thenReturnUpdatedDataEntity() throws ServiceException {
        // Arrange
        when(modelEntityRepository.existsById(a9aModel.getId())).thenReturn(true);
        doReturn(a9aModelAnother).when(modelEntityRepository).save(a9aModel);

        // Act
        ModelEntity found = modelService.updateModel(a9aModel);

        // Assert
        assertAll(
                () -> verify(modelEntityRepository, times(1)).save(a9aModel),
                () -> assertEquals(a9aModelAnother, found)
        );
    }

    @Test
    public void updateModel_givenNullDataEntity_thenThrowNullParameterException() {
        // Assert
        // Act
        assertAll(
                () -> assertThrows(NullParameterException.class, () -> modelService.updateModel(nullModel)),
                () -> assertThrows(NullParameterException.class, () -> modelService.updateModel(nullIdModel)),
                () -> assertThrows(NullParameterException.class, () -> modelService.updateModel(nullDataBytesModel))
        );
    }

    @Test
    public void updateModel_givenNonExistName_thenThrowElementNotExistException() {
        // Arrange
        when(modelEntityRepository.existsById(a9aModel.getId())).thenReturn(false);

        // Assert
        // Act
        assertThrows(ElementNotExistException.class, () -> modelService.updateModel(a9aModel));
    }

    // =====================================================================
    // deleteModelById
    // =====================================================================

    @Test
    public void deleteModelById_thenCallDeleteByNameOfDataEntityRepository() throws ServiceException {
        // Arrange
        when(modelEntityRepository.existsById(a9aModel.getId())).thenReturn(true);
        doNothing().when(modelEntityRepository).deleteById(a9aModel.getId());

        // Act
        modelService.deleteModelById(a9aModel.getId());

        // Assert
        verify(modelEntityRepository, times(1)).deleteById(a9aModel.getId());
    }

    @Test
    public void deleteModelById_givenNullName_thenThrowNullParameterException() {
        // Assert
        // Act
        assertThrows(NullParameterException.class, () -> modelService.deleteModelById(null));
    }

    @Test
    public void deleteModelById_givenNonExistName_thenThrowElementNotExistException() {
        // Arrange
        when(modelEntityRepository.existsById(a9aModel.getId())).thenReturn(false);
        doNothing().when(modelEntityRepository).deleteById(a9aModel.getId());

        // Assert
        // Act
        assertThrows(ElementNotExistException.class, () -> modelService.deleteModelById(a9aModel.getId()));
    }
}
