package io.hychou.libsvm.predict.service.impl;

import io.hychou.common.exception.service.clienterror.IllegalParameterException;
import io.hychou.data.entity.DataEntity;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.parameter.LibsvmPredictParameterEntity;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PredictServiceImplTest {

    @Autowired
    private PredictServiceImpl predictService;

    private DataEntity data;
    private ModelEntity defaultModel;
    private ModelEntity probModel;
    private ModelEntity probESvrModel;

    @Before
    public void setUp() throws Exception {
        File heartScale = ResourceUtils.getFile("classpath:data/heart_scale");
        File heartScaleDefaultModel = ResourceUtils.getFile("classpath:model/heart_scale_default_model");
        File heartScaleProbModel = ResourceUtils.getFile("classpath:model/heart_scale_prob_model");
        File heartScaleProbESvrModel = ResourceUtils.getFile("classpath:model/heart_scale_prob_esvr_model");

        data = new DataEntity();
        data.setName("data");
        data.setDataBytes(Files.readAllBytes(heartScale.toPath()));

        defaultModel = new ModelEntity();
        defaultModel.setId(1L);
        defaultModel.setDataBytes(Files.readAllBytes(heartScaleDefaultModel.toPath()));

        probModel = new ModelEntity();
        probModel.setId(2L);
        probModel.setDataBytes(Files.readAllBytes(heartScaleProbModel.toPath()));

        probESvrModel = new ModelEntity();
        probESvrModel.setId(3L);
        probESvrModel.setDataBytes(Files.readAllBytes(heartScaleProbESvrModel.toPath()));
    }

    @Test
    public void svmPredict_givenDefaultModel_returnCorrectPredictionEntity() throws Exception {
        // Arrange
        File expectedPrediction = ResourceUtils.getFile("classpath:prediction/heart_scale_default_prediction");
        List<String> expected = Arrays.asList(new String(Files.readAllBytes(expectedPrediction.toPath())).split(LIBSVM_DELIMITERS));
        // Apply
        byte[] bytes = predictService.svmPredict(data, defaultModel, new LibsvmPredictParameterEntity(false)).getDataBytes();
        List<String> actual = Arrays.asList((new String(bytes)).split(LIBSVM_DELIMITERS));
        // Assert
        assertListOfLibsvmTokenEquals(expected, actual);
    }

    @Test
    public void svmPredict_givenProbModel_returnCorrectPredictionEntity() throws Exception {
        // Arrange
        File expectedPrediction = ResourceUtils.getFile("classpath:prediction/heart_scale_prob_prediction");
        List<String> expected = Arrays.asList(new String(Files.readAllBytes(expectedPrediction.toPath())).split(LIBSVM_DELIMITERS));
        // Apply
        byte[] bytes = predictService.svmPredict(data, probModel, new LibsvmPredictParameterEntity(true)).getDataBytes();
        List<String> actual = Arrays.asList((new String(bytes)).split(LIBSVM_DELIMITERS));
        // Assert
        assertListOfLibsvmTokenEquals(expected, actual, 1e-2);
    }

    @Test
    public void svmPredict_givenProbModelAndNoPredictProb_returnCorrectPredictionEntity() throws Exception {
        // Arrange
        File expectedPrediction = ResourceUtils.getFile("classpath:prediction/heart_scale_default_prediction");
        List<String> expected = Arrays.asList(new String(Files.readAllBytes(expectedPrediction.toPath())).split(LIBSVM_DELIMITERS));
        // Apply
        byte[] bytes = predictService.svmPredict(data, probModel, new LibsvmPredictParameterEntity(false)).getDataBytes();
        List<String> actual = Arrays.asList((new String(bytes)).split(LIBSVM_DELIMITERS));
        // Assert
        assertListOfLibsvmTokenEquals(expected, actual, 1e-2);
    }

    @Test
    public void svmPredict_givenDefaultModelAndPredictProb_shouldThrowIllegalParameterException() {
        // Arrange
        // Apply
        // Assert
        assertThrows(IllegalParameterException.class, () ->
                predictService.svmPredict(data, defaultModel, new LibsvmPredictParameterEntity(true)));
    }

    @Test
    public void svmPredict_givenSvrAndPredictProb_returnCorrectPredictionEntity() throws Exception {
        // Arrange
        File expectedPrediction = ResourceUtils.getFile("classpath:prediction/heart_scale_prob_esvr_prediction");
        List<String> expected = Arrays.asList(new String(Files.readAllBytes(expectedPrediction.toPath())).split(LIBSVM_DELIMITERS));
        // Apply
        byte[] bytes = predictService.svmPredict(data, probESvrModel, new LibsvmPredictParameterEntity(true)).getDataBytes();
        List<String> actual = Arrays.asList((new String(bytes)).split(LIBSVM_DELIMITERS));
        // Assert
        assertListOfLibsvmTokenEquals(expected, actual, 1e-2);
    }
}
