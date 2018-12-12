package io.hychou.data.service;

import io.hychou.common.exception.ServiceException;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.entity.DataInfo;

import java.util.List;

public interface DataService {
    List<DataInfo> listDataInfo();

    DataEntity readDataByName(String name) throws ServiceException;

    DataEntity createData(DataEntity dataEntity) throws ServiceException;

    DataEntity updateData(DataEntity dataEntity) throws ServiceException;

    void deleteDataByName(String name) throws ServiceException;
}
