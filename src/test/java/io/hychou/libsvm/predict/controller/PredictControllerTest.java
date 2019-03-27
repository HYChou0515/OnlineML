package io.hychou.libsvm.predict.controller;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.service.DataService;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.model.service.ModelService;
import io.hychou.libsvm.parameter.LibsvmPredictParameterEntity;
import io.hychou.libsvm.predict.service.PredictService;
import io.hychou.libsvm.prediction.entity.PredictionEntity;
import io.hychou.libsvm.prediction.service.PredictionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(PredictController.class)
public class PredictControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    private DataService dataService;
    @MockBean
    private ModelService modelService;
    @MockBean
    private PredictService predictService;
    @MockBean
    private PredictionService predictionService;

    private DataEntity a9a;
    private ModelEntity a9aModel;
    private PredictionEntity a9aPrediction;
    private PredictionEntity a9aPredictionNoId;
    private LibsvmPredictParameterEntity libsvmPredictParameterEntity;

    private static final String MOCK_EXCEPTION_ERROR_MESSAGE = "This is a mock exception";
    private ServiceException mockException = new ServiceException(MOCK_EXCEPTION_ERROR_MESSAGE) {
        @Override
        public HttpStatus getHttpStatus() {
            return HttpStatus.I_AM_A_TEAPOT;
        }
    };
    @Before
    public void setUp() {
        a9a = new DataEntity();
        a9a.setName("a9a");

        a9aModel = new ModelEntity();
        a9aModel.setId(1L);
        a9aModel.setDataBytes("This is a9a model".getBytes());

        a9aPrediction = new PredictionEntity();
        a9aPrediction.setId(2L);
        a9aPrediction.setDataBytes("Prediction of a9a".getBytes());

        a9aPredictionNoId = new PredictionEntity();
        a9aPredictionNoId.setId(null);
        a9aPredictionNoId.setDataBytes("Prediction of a9a".getBytes());

        libsvmPredictParameterEntity = new LibsvmPredictParameterEntity();
    }

    // =====================================================================
    // svmPredict
    // =====================================================================

    @Test
    public void svmTrain_theReturnProperResponseEntity() throws Exception {
        // Arrange
        given(dataService.readDataByName(a9a.getName())).willReturn(a9a);
        given(modelService.readModelById(a9aModel.getId())).willReturn(a9aModel);
        given(predictService.svmPredict(a9a, a9aModel, libsvmPredictParameterEntity)).willReturn(a9aPredictionNoId);
        given(predictionService.createPrediction(a9aPredictionNoId)).willReturn(a9aPrediction);

        // Act
        MockHttpServletResponse response = mvc.perform(
                get(svmPredictUrl(a9a.getName(), a9aModel.getId(), libsvmPredictParameterEntity))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Arrange
        assertEquals(response.getContentAsString(), a9aPrediction.getId().toString());
    }

    private String svmPredictUrl(String dataName, long modelId, LibsvmPredictParameterEntity libsvmPredictParameterEntity) {
        StringBuilder sb = new StringBuilder("/predict/");
        sb.append(dataName);
        sb.append("/");
        sb.append(modelId);
        sb.append("?");
        StringJoiner requestParams = new StringJoiner("&");
        if(Objects.nonNull(libsvmPredictParameterEntity.getProbabilityEstimates()))
            requestParams.add("probabilityEstimates="+ libsvmPredictParameterEntity.getProbabilityEstimates());
        sb.append(requestParams.toString());
        return sb.toString();
    }
}
