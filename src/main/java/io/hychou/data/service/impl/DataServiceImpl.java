package io.hychou.data.service.impl;

import io.hychou.common.exception.IllegalArgumentException;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.clienterror.ElementAlreadyExistException;
import io.hychou.common.exception.service.clienterror.ElementNotExistException;
import io.hychou.common.exception.service.clienterror.IllegalParameterException;
import io.hychou.common.exception.service.clienterror.NullParameterException;
import io.hychou.data.dao.DataEntityRepository;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.entity.DataInfo;
import io.hychou.data.service.DataService;
import io.hychou.data.util.DataUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DataServiceImpl implements DataService {
    private final DataEntityRepository dataEntityRepository;
    private static final String ID_STRING = "Name";

    public DataServiceImpl(DataEntityRepository dataEntityRepository) {
        this.dataEntityRepository = dataEntityRepository;
    }

    @Override
    public List<DataInfo> listDataInfo() {
        return dataEntityRepository.findDataInfoBy();
    }

    @Override
    public DataEntity readDataByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new DataEntity().getStringQueryWithNullParam(ID_STRING));
        }
        Optional<DataEntity> dataEntity = dataEntityRepository.findByName(name);
        if (dataEntity.isPresent()) {
            return dataEntity.get();
        } else {
            throw new ElementNotExistException(new DataEntity().getStringNotExistForParam(ID_STRING, name));
        }
    }

    @Override
    public DataEntity createData(DataEntity dataEntity) throws ServiceException {
        if (dataEntity == null || dataEntity.getName() == null || dataEntity.getDataBytes() == null) {
            throw new NullParameterException(new DataEntity().getStringCreateNull());
        }
        checkData(dataEntity.getDataBytes());
        if (dataEntityRepository.existsByName(dataEntity.getName())) {
            throw new ElementAlreadyExistException(new DataEntity().getStringCreateExistingForParam(ID_STRING, dataEntity.getName()));
        }
        dataEntity = dataEntityRepository.save(dataEntity);
        return dataEntity;
    }

    @Override
    public DataEntity updateData(DataEntity dataEntity) throws ServiceException {
        if (dataEntity == null || dataEntity.getName() == null || dataEntity.getDataBytes() == null) {
            throw new NullParameterException(new DataEntity().getStringUpdateNull());
        }
        checkData(dataEntity.getDataBytes());
        if (!dataEntityRepository.existsByName(dataEntity.getName())) {
            throw new ElementNotExistException(new DataEntity().getStringNotExistForParam(ID_STRING, dataEntity.getName()));
        }
        dataEntity = dataEntityRepository.save(dataEntity);
        return dataEntity;
    }

    @Override
    public void deleteDataByName(String name) throws ServiceException {
        if (name == null) {
            throw new NullParameterException(new DataEntity().getStringDeleteWithNullParam(ID_STRING));
        }
        if (!dataEntityRepository.existsByName(name)) {
            throw new ElementNotExistException(new DataEntity().getStringNotExistForParam(ID_STRING, name));
        }
        dataEntityRepository.deleteByName(name);
    }

    private void checkData(byte[] dataBytes) throws ServiceException {
        try {
            DataUtils.checkData(dataBytes);
        } catch (IOException e) {
            throw new IllegalParameterException("Cannot read data", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalParameterException("Data format not correct", e);
        }
    }
}
