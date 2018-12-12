package io.hychou.data.service.impl;

import io.hychou.common.exception.ServiceException;
import io.hychou.common.exception.clienterror.ElementAlreadyExistException;
import io.hychou.common.exception.clienterror.ElementNotExistException;
import io.hychou.common.exception.clienterror.NullParameterException;
import io.hychou.data.dao.DataEntityRepository;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.entity.DataInfo;
import io.hychou.data.service.DataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class DataServiceImplTest {

    private DataEntityRepository dataEntityRepository = Mockito.mock(DataEntityRepository.class);
    private DataService dataService = new DataServiceImpl(dataEntityRepository);

    private DataEntity a9a;
    private DataEntity rcv1;
    private DataEntity a9aOther;
    private List<DataInfo> dataInfoList;

    @Before
    public void setUp() {
        a9a = new DataEntity();
        a9a.setName("a9a");
        a9a.setDataBytes("This is a9a".getBytes());

        rcv1 = new DataEntity();
        rcv1.setName("rcv1");
        rcv1.setDataBytes("This is a9a".getBytes());

        a9aOther = new DataEntity();
        a9aOther.setName("a9aOther");
        a9aOther.setDataBytes("This is a9a".getBytes());

        dataInfoList = Arrays.asList(
                new DataInfoEntity(a9a.getName()),
                new DataInfoEntity(rcv1.getName()),
                new DataInfoEntity(a9aOther.getName())
        );
    }

    private class DataInfoEntity implements DataInfo {
        private String name;

        DataInfoEntity(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return null;
        }
    }

    // =====================================================================
    // listDataInfo
    // =====================================================================

    @Test
    public void listDataInfo_thenReturnListOfDataInfo() {
        // Arrange
        when(dataEntityRepository.findDataInfoBy()).thenReturn(dataInfoList);

        // Act
        List<DataInfo> found = dataService.listDataInfo();

        // Assert
        assertIterableEquals(dataInfoList, found);
    }

    // =====================================================================
    // readDataByName
    // =====================================================================

    @Test
    public void readDataByName_thenReturnDataEntity() throws ServiceException {
        // Arrange
        when(dataEntityRepository.findByName(a9a.getName())).thenReturn(Optional.of(a9a));

        // Act
        DataEntity found = dataService.readDataByName(a9a.getName());

        // Assert
        assertEquals(a9a, found);
    }

    @Test
    public void readDataByName_givenNullName_thenThrowNullParameterException() {
        // Assert
        // Act
        assertThrows(NullParameterException.class, () -> dataService.readDataByName(null));
    }

    @Test
    public void readDataByName_givenNonExistName_thenThrowElementNotExistException() {
        // Arrange
        when(dataEntityRepository.findByName(a9a.getName())).thenReturn(Optional.empty());

        // Assert
        // Act
        assertThrows(ElementNotExistException.class, () -> dataService.readDataByName(a9a.getName()));
    }

    // =====================================================================
    // createData
    // =====================================================================

    @Test
    public void createData_thenReturnSavedDataEntity() throws ServiceException {
        // Arrange
        doReturn(a9aOther).when(dataEntityRepository).save(a9a);

        // Act
        DataEntity found = dataService.createData(a9a);

        // Assert
        assertAll(
                () -> verify(dataEntityRepository, times(1)).save(a9a),
                () -> assertEquals(a9aOther, found)
        );
    }

    @Test
    public void createData_givenNullDataEntity_thenThrowNullParameterException() {
        // Assert
        // Act
        assertThrows(NullParameterException.class, () -> dataService.createData(null));
    }

    @Test
    public void createData_givenAlreadyExistName_thenThrowElementAlreadyExistException() {
        // Arrange
        doReturn(true).when(dataEntityRepository).existsByName(a9a.getName());

        // Assert
        // Act
        assertThrows(ElementAlreadyExistException.class, () -> dataService.createData(a9a));
    }

    // =====================================================================
    // updateData
    // =====================================================================

    @Test
    public void updateData_thenReturnUpdatedDataEntity() throws ServiceException {
        // Arrange
        when(dataEntityRepository.existsByName(a9a.getName())).thenReturn(true);
        doReturn(a9aOther).when(dataEntityRepository).save(a9a);

        // Act
        DataEntity found = dataService.updateData(a9a);

        // Assert
        assertAll(
                () -> verify(dataEntityRepository, times(1)).save(a9a),
                () -> assertEquals(a9aOther, found)
        );
    }

    @Test
    public void updateData_givenNullDataEntity_thenThrowNullParameterException() {
        // Assert
        // Act
        assertThrows(NullParameterException.class, () -> dataService.updateData(null));
    }

    @Test
    public void updateData_givenNonExistName_thenThrowElementNotExistException() {
        // Arrange
        when(dataEntityRepository.existsByName(a9a.getName())).thenReturn(false);

        // Assert
        // Act
        assertThrows(ElementNotExistException.class, () -> dataService.updateData(a9a));
    }

    // =====================================================================
    // deleteDataByName
    // =====================================================================

    @Test
    public void deleteDataByName_thenCallDeleteByNameOfDataEntityRepository() throws ServiceException {
        // Arrange
        when(dataEntityRepository.existsByName(a9a.getName())).thenReturn(true);
        doNothing().when(dataEntityRepository).deleteByName(a9a.getName());

        // Act
        dataService.deleteDataByName(a9a.getName());

        // Assert
        verify(dataEntityRepository, times(1)).deleteByName(a9a.getName());
    }

    @Test
    public void deleteDataByName_givenNullName_thenThrowNullParameterException() {
        // Assert
        // Act
        assertThrows(NullParameterException.class, () -> dataService.deleteDataByName(null));
    }

    @Test
    public void deleteDataByName_givenNonExistName_thenThrowElementNotExistException() {
        // Arrange
        when(dataEntityRepository.existsByName(a9a.getName())).thenReturn(false);
        doNothing().when(dataEntityRepository).deleteByName(a9a.getName());

        // Assert
        // Act
        assertThrows(ElementNotExistException.class, () -> dataService.deleteDataByName(a9a.getName()));
    }
}
