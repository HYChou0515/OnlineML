package io.hychou.libsvm.train.service.impl;

import io.hychou.data.entity.DataEntity;
import io.hychou.libsvm.parameter.LibsvmTrainParameterEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static io.hychou.common.Constant.LIBSVM_DELIMITERS;
import static io.hychou.common.util.AssertUtils.assertListOfLibsvmTokenEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TrainServiceImplTest {

    @Autowired
    private TrainServiceImpl trainService;

    private LibsvmTrainParameterEntity libsvmTrainParameterEntity;
    private DataEntity data;

    @Before
    public void setUp() throws Exception {
        libsvmTrainParameterEntity = new LibsvmTrainParameterEntity();

        File heartScale = ResourceUtils.getFile("classpath:data/heart_scale");

        data = new DataEntity();
        data.setName("data");
        data.setDataBytes(Files.readAllBytes(heartScale.toPath()));
    }

    @Test
    public void svmTrain_returnCorrectModelEntity() throws Exception {
        // Arrange
        File expectedModel = ResourceUtils.getFile("classpath:model/heart_scale_default_model");
        List<String> expected = Arrays.asList(new String(Files.readAllBytes(expectedModel.toPath())).split(LIBSVM_DELIMITERS));
        // Apply
        byte[] bytes = trainService.svmTrain(data, libsvmTrainParameterEntity).getDataBytes();
        List<String> actual = Arrays.asList((new String(bytes)).split(LIBSVM_DELIMITERS)); // libsvm uses "\n" for line separator
        // Assert
        assertListOfLibsvmTokenEquals(expected, actual, 1e-2);
    }
}
