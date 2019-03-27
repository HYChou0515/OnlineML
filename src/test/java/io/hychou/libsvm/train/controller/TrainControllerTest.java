package io.hychou.libsvm.train.controller;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.service.DataService;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.model.service.ModelService;
import io.hychou.libsvm.parameter.KernelTypeEnum;
import io.hychou.libsvm.parameter.LibsvmTrainParameterEntity;
import io.hychou.libsvm.parameter.SvmTypeEnum;
import io.hychou.libsvm.train.service.TrainService;
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
@WebMvcTest(TrainController.class)
public class TrainControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    private TrainService trainService;
    @MockBean
    private ModelService modelService;
    @MockBean
    private DataService dataService;

    private DataEntity a9a;
    private ModelEntity a9aModel;
    private ModelEntity a9aModelNoId;
    private LibsvmTrainParameterEntity libsvmTrainParameterEntity;

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

        a9aModelNoId = new ModelEntity();
        a9aModelNoId.setId(null);
        a9aModelNoId.setDataBytes("This is a9a model".getBytes());

        libsvmTrainParameterEntity = LibsvmTrainParameterEntity.build()
                .svmType(SvmTypeEnum.C_SVC)
                .kernelType(KernelTypeEnum.RBF)
                .degree(1)
                .gamma(1.0)
                .coef0(2.0)
                .cacheSize(3.0)
                .eps(4.0)
                .c(5.0)
                .nu(6.0)
                .p(7.0)
                .shrinking(true)
                .probability(false).done();
    }

    // =====================================================================
    // svmTrain
    // =====================================================================

    @Test
    public void svmTrain_theReturnProperResponseEntity() throws Exception {
        // Arrange
        given(dataService.readDataByName(a9a.getName())).willReturn(a9a);
        given(trainService.svmTrain(a9a, libsvmTrainParameterEntity)).willReturn(a9aModelNoId);
        given(modelService.createModel(a9aModelNoId)).willReturn(a9aModel);

        // Act
        svmTrainUrl(a9a.getName(), libsvmTrainParameterEntity);
        MockHttpServletResponse response = mvc.perform(
                get(svmTrainUrl(a9a.getName(), libsvmTrainParameterEntity))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Arrange
        assertEquals(response.getContentAsString(), a9aModel.getId().toString());

    }

    private String svmTrainUrl(String dataName, LibsvmTrainParameterEntity libsvmTrainParameterEntity) {
        StringBuilder sb = new StringBuilder("/train/");
        sb.append(dataName);
        sb.append("?");
        StringJoiner requestParams = new StringJoiner("&");
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("svmType="+ libsvmTrainParameterEntity.getSvmType());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("kernelType="+ libsvmTrainParameterEntity.getKernelType());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("degree="+ libsvmTrainParameterEntity.getDegree());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("gamma="+ libsvmTrainParameterEntity.getGamma());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("coef0="+ libsvmTrainParameterEntity.getCoef0());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("cacheSize="+ libsvmTrainParameterEntity.getCacheSize());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("eps="+ libsvmTrainParameterEntity.getEps());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("c="+ libsvmTrainParameterEntity.getC());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("nu="+ libsvmTrainParameterEntity.getNu());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("p="+ libsvmTrainParameterEntity.getP());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("shrinking="+ libsvmTrainParameterEntity.getShrinking());
        if(Objects.nonNull(libsvmTrainParameterEntity.getSvmType()))
            requestParams.add("probability="+ libsvmTrainParameterEntity.getProbability());
        sb.append(requestParams.toString());
        return sb.toString();
    }
}
