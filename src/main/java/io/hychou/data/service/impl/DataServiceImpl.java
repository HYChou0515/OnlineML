package io.hychou.data.service.impl;

import io.hychou.common.exception.ServiceException;
import io.hychou.common.exception.clienterror.ElementAlreadyExistException;
import io.hychou.common.exception.clienterror.ElementNotExistException;
import io.hychou.common.exception.clienterror.NullParameterException;
import io.hychou.data.dao.DataEntityRepository;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.entity.DataInfo;
import io.hychou.data.service.DataService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DataServiceImpl implements DataService {
    private final DataEntityRepository dataEntityRepository;

    public DataServiceImpl(DataEntityRepository dataEntityRepository) {
        this.dataEntityRepository = dataEntityRepository;
    }

    private static final String DOES_NOT_EXIST = " does not exist";

    @Override
    public List<DataInfo> listDataInfo() {
        return dataEntityRepository.findDataInfoBy();
    }

    @Override
    public DataEntity readDataByName(String name) throws ServiceException {
        if(name == null) {
            throw new NullParameterException("Trying to query with null name");
        }
        Optional<DataEntity> dataEntity = dataEntityRepository.findByName(name);
        if(dataEntity.isPresent()) {
            return dataEntity.get();
        } else {
            throw new ElementNotExistException("Data with name="+name+DOES_NOT_EXIST);
        }
    }

    @Override
    public DataEntity createData(DataEntity dataEntity) throws ServiceException {
        if (dataEntity == null || dataEntity.getName() == null || dataEntity.getDataBytes() == null) {
            throw new NullParameterException("Trying to create null data");
        }
        if(dataEntityRepository.existsByName(dataEntity.getName())) {
            throw new ElementAlreadyExistException("Trying to create data with existing name="+dataEntity.getName());
        }
        dataEntity = dataEntityRepository.save(dataEntity);
        return dataEntity;
    }

    @Override
    public DataEntity updateData(DataEntity dataEntity) throws ServiceException {
        if (dataEntity == null || dataEntity.getName() == null || dataEntity.getDataBytes() == null) {
            throw new NullParameterException("Trying to update for null data");
        }
        if( ! dataEntityRepository.existsByName(dataEntity.getName())) {
            throw new ElementNotExistException("Data with name="+dataEntity.getName()+DOES_NOT_EXIST);
        }
        dataEntity = dataEntityRepository.save(dataEntity);
        return dataEntity;
    }

    @Override
    public void deleteDataByName(String name) throws ServiceException {
        if(name == null) {
            throw new NullParameterException("Trying to delete data with empty name");
        }
        if( ! dataEntityRepository.existsByName(name)) {
            throw new ElementNotExistException("Data with id="+name+DOES_NOT_EXIST);
        }
        dataEntityRepository.deleteByName(name);
    }
}
