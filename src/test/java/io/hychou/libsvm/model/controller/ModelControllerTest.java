package io.hychou.libsvm.model.controller;

import com.google.common.net.HttpHeaders;
import io.hychou.common.ControllerTest;
import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.ServiceException;
import io.hychou.libsvm.model.entity.ModelEntity;
import io.hychou.libsvm.model.service.ModelService;
import io.hychou.common.Constant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@RunWith(SpringRunner.class)
@WebMvcTest(ModelController.class)
public class ModelControllerTest extends ControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    private ModelService modelService;

    private ModelEntity a9aModel;
    private ModelEntity a9aModelAnother;

    private static final String MOCK_EXCEPTION_ERROR_MESSAGE = "This is a mock exception";
    private ServiceException mockException = new ServiceException(MOCK_EXCEPTION_ERROR_MESSAGE) {
        @Override
        public HttpStatus getHttpStatus() {
            return HttpStatus.I_AM_A_TEAPOT;
        }
    };

    @Before
    public void setUp() {
        a9aModel = new ModelEntity();
        a9aModel.setId(1L);
        a9aModel.setDataBytes("This is a9a model".getBytes());

        a9aModelAnother = new ModelEntity();
        a9aModelAnother.setId(null);
        a9aModelAnother.setDataBytes("This is a9a model".getBytes());
    }

    // =====================================================================
    // readModelById
    // =====================================================================

    @Test
    public void readModelById_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        given(modelService.readModelById(a9aModel.getId())).willReturn(a9aModel);

        // Act
        MockHttpServletResponse response = mvc.perform(
                get("/model/"+a9aModel.getId()).accept(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(new String(a9aModel.getDataBytes()), response.getContentAsString()),
                () -> assertEquals("attachment; filename=\""+a9aModel.getFileName()+"\"", response.getHeader(HttpHeaders.CONTENT_DISPOSITION))
        );
    }

    @Test
    public void readModelById_givenServiceException_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        given(modelService.readModelById(a9aModel.getId())).willThrow(mockException);

        // Act
        MockHttpServletResponse response = mvc.perform(
                get("/model/"+a9aModel.getId()).accept(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getStatus()),
                () -> assertEquals(Constant.EMPTY_STRING, response.getContentAsString()),
                () -> assertEquals(MOCK_EXCEPTION_ERROR_MESSAGE, response.getHeader(MessageResponseEntity.HTTP_HEADER_STATUS_MESSAGE))
        );
    }

    // =====================================================================
    // createModel
    // =====================================================================

    @Test
    public void createModel_thenReturnProperResponseEntity () throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9aModelAnother.getDataBytes());
        given(modelService.createModel(a9aModelAnother)).willReturn(a9aModel);

        // Act
        MockHttpServletResponse response = mvc.perform(
                multipart("/model").file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> then(modelService).should(times(1)).createModel(a9aModelAnother)
        );
    }

    @Test
    public void createModel_givenServiceException_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9aModelAnother.getDataBytes());
        given(modelService.createModel(a9aModelAnother)).willThrow(mockException);

        // Act
        MockHttpServletResponse response = mvc.perform(
                multipart("/model").file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getStatus()),
                () -> assertEquals(Constant.EMPTY_STRING, response.getContentAsString()),
                () -> assertEquals(MOCK_EXCEPTION_ERROR_MESSAGE, response.getHeader(MessageResponseEntity.HTTP_HEADER_STATUS_MESSAGE))
        );
    }

    // =====================================================================
    // updateModelById
    // =====================================================================

    @Test
    public void updateData_thenReturnProperResponseEntity () throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9aModel.getDataBytes());
        given(modelService.updateModel(a9aModel)).willReturn(a9aModelAnother);

        // Act
        MockHttpServletResponse response = mvc.perform(
                putMultipart("/model/"+a9aModel.getId()).file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> then(modelService).should(times(1)).updateModel(a9aModel)
        );
    }

    @Test
    public void updateData_givenServiceException_thenReturnProperResponseEntity () throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9aModel.getDataBytes());
        given(modelService.updateModel(a9aModel)).willThrow(mockException);

        // Act
        MockHttpServletResponse response = mvc.perform(
                putMultipart("/model/"+a9aModel.getId()).file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getStatus()),
                () -> assertEquals(Constant.EMPTY_STRING, response.getContentAsString()),
                () -> assertEquals(MOCK_EXCEPTION_ERROR_MESSAGE, response.getHeader(MessageResponseEntity.HTTP_HEADER_STATUS_MESSAGE))
        );
    }

    // =====================================================================
    // deleteModelById
    // =====================================================================

    @Test
    public void deleteDataByName_thenReturnProperResponseEntity () throws Exception {
        // Arrange
        doNothing().when(modelService).deleteModelById(a9aModel.getId());

        // Act
        MockHttpServletResponse response = mvc.perform(
                delete("/model/"+a9aModel.getId()))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> then(modelService).should(times(1)).deleteModelById(a9aModel.getId())
        );
    }

    @Test
    public void deleteDataByName_givenServiceException_thenReturnProperResponseEntity () throws Exception {
        // Arrange
        doThrow(mockException).when(modelService).deleteModelById(a9aModel.getId());

        // Act
        MockHttpServletResponse response = mvc.perform(
                delete("/model/"+a9aModel.getId()))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getStatus()),
                () -> assertEquals(Constant.EMPTY_STRING, response.getContentAsString()),
                () -> assertEquals(MOCK_EXCEPTION_ERROR_MESSAGE, response.getHeader(MessageResponseEntity.HTTP_HEADER_STATUS_MESSAGE))
        );
    }
}
