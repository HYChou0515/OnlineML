package io.hychou.libsvm.train.service.impl;

import io.hychou.data.entity.DataEntity;
import io.hychou.libsvm.parameter.LibsvmParameterEntity;
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

import static io.hychou.common.Constant.UNIX_LINE_SEPARATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TrainServiceTest {

    @Autowired
    private TrainServiceImpl trainService;

    private LibsvmParameterEntity libsvmParameterEntity;
    private DataEntity data;

    @Before
    public void setUp() throws Exception {
        libsvmParameterEntity = new LibsvmParameterEntity();

        File heartScale = ResourceUtils.getFile("classpath:data/heart_scale");

        data = new DataEntity();
        data.setName("data");
        data.setDataBytes(Files.readAllBytes(heartScale.toPath()));
    }

    @Test
    public void svmTrain_returnCorrectModelEntity() throws Exception {
        // Arrange
        File expectedModel = ResourceUtils.getFile("classpath:model/heart_scale_default_model");
        List<String> expected = Files.readAllLines(expectedModel.toPath());
        // Apply
        byte[] bytes = trainService.svmTrain(data, libsvmParameterEntity).getDataBytes();
        List<String> actual = Arrays.asList((new String(bytes)).split(UNIX_LINE_SEPARATOR)); // libsvm uses "\n" for line separator
        // Assert
        assertEquals(expected, actual);
    }
}
