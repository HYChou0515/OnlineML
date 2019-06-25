package io.hychou.libsvm.prediction.controller;

import com.google.common.net.HttpHeaders;
import io.hychou.common.Constant;
import io.hychou.common.MessageResponseEntity;
import io.hychou.common.exception.service.ServiceException;
import io.hychou.libsvm.prediction.entity.PredictionEntity;
import io.hychou.libsvm.prediction.service.PredictionService;
import io.hychou.test.common.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(PredictionController.class)
public class PredictionControllerTest extends ControllerTest {

    private static final String MOCK_EXCEPTION_ERROR_MESSAGE = "This is a mock exception";
    @Autowired
    MockMvc mvc;
    @MockBean
    private PredictionService predictionService;
    private PredictionEntity a9aPrediction;
    private PredictionEntity a9aPredictionAnother;
    private ServiceException mockException = new ServiceException(MOCK_EXCEPTION_ERROR_MESSAGE) {
        @Override
        public HttpStatus getHttpStatus() {
            return HttpStatus.I_AM_A_TEAPOT;
        }
    };

    @BeforeEach
    public void setUp() {
        a9aPrediction = new PredictionEntity();
        a9aPrediction.setId(1L);
        a9aPrediction.setDataBytes("This is a9a prediction".getBytes());

        a9aPredictionAnother = new PredictionEntity();
        a9aPredictionAnother.setId(null);
        a9aPredictionAnother.setDataBytes("This is a9a prediction".getBytes());
    }

    // =====================================================================
    // readPredictionById
    // =====================================================================

    @Test
    public void readPredictionById_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        given(predictionService.readPredictionById(a9aPrediction.getId())).willReturn(a9aPrediction);

        // Act
        MockHttpServletResponse response = mvc.perform(
                get("/prediction/" + a9aPrediction.getId()).accept(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(new String(a9aPrediction.getDataBytes()), response.getContentAsString()),
                () -> assertEquals("attachment; filename=\"" + a9aPrediction.getFileName() + "\"", response.getHeader(HttpHeaders.CONTENT_DISPOSITION))
        );
    }

    @Test
    public void readPredictionById_givenServiceException_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        given(predictionService.readPredictionById(a9aPrediction.getId())).willThrow(mockException);

        // Act
        MockHttpServletResponse response = mvc.perform(
                get("/prediction/" + a9aPrediction.getId()).accept(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getStatus()),
                () -> assertEquals(Constant.EMPTY_STRING, response.getContentAsString()),
                () -> assertEquals(MOCK_EXCEPTION_ERROR_MESSAGE, response.getHeader(MessageResponseEntity.HTTP_HEADER_STATUS_MESSAGE))
        );
    }

    // =====================================================================
    // createPrediction
    // =====================================================================

    @Test
    public void createPrediction_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9aPredictionAnother.getDataBytes());
        given(predictionService.createPrediction(a9aPredictionAnother)).willReturn(a9aPrediction);

        // Act
        MockHttpServletResponse response = mvc.perform(
                multipart("/prediction").file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> then(predictionService).should(times(1)).createPrediction(a9aPredictionAnother)
        );
    }

    @Test
    public void createPrediction_givenServiceException_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9aPredictionAnother.getDataBytes());
        given(predictionService.createPrediction(a9aPredictionAnother)).willThrow(mockException);

        // Act
        MockHttpServletResponse response = mvc.perform(
                multipart("/prediction").file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getStatus()),
                () -> assertEquals(Constant.EMPTY_STRING, response.getContentAsString()),
                () -> assertEquals(MOCK_EXCEPTION_ERROR_MESSAGE, response.getHeader(MessageResponseEntity.HTTP_HEADER_STATUS_MESSAGE))
        );
    }

    // =====================================================================
    // updatePredictionById
    // =====================================================================

    @Test
    public void updateData_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9aPrediction.getDataBytes());
        given(predictionService.updatePrediction(a9aPrediction)).willReturn(a9aPredictionAnother);

        // Act
        MockHttpServletResponse response = mvc.perform(
                putMultipart("/prediction/" + a9aPrediction.getId()).file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> then(predictionService).should(times(1)).updatePrediction(a9aPrediction)
        );
    }

    @Test
    public void updateData_givenServiceException_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9aPrediction.getDataBytes());
        given(predictionService.updatePrediction(a9aPrediction)).willThrow(mockException);

        // Act
        MockHttpServletResponse response = mvc.perform(
                putMultipart("/prediction/" + a9aPrediction.getId()).file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getStatus()),
                () -> assertEquals(Constant.EMPTY_STRING, response.getContentAsString()),
                () -> assertEquals(MOCK_EXCEPTION_ERROR_MESSAGE, response.getHeader(MessageResponseEntity.HTTP_HEADER_STATUS_MESSAGE))
        );
    }

    // =====================================================================
    // deletePredictionById
    // =====================================================================

    @Test
    public void deleteDataByName_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        doNothing().when(predictionService).deletePredictionById(a9aPrediction.getId());

        // Act
        MockHttpServletResponse response = mvc.perform(
                delete("/prediction/" + a9aPrediction.getId()))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> then(predictionService).should(times(1)).deletePredictionById(a9aPrediction.getId())
        );
    }

    @Test
    public void deleteDataByName_givenServiceException_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        doThrow(mockException).when(predictionService).deletePredictionById(a9aPrediction.getId());

        // Act
        MockHttpServletResponse response = mvc.perform(
                delete("/prediction/" + a9aPrediction.getId()))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), response.getStatus()),
                () -> assertEquals(Constant.EMPTY_STRING, response.getContentAsString()),
                () -> assertEquals(MOCK_EXCEPTION_ERROR_MESSAGE, response.getHeader(MessageResponseEntity.HTTP_HEADER_STATUS_MESSAGE))
        );
    }

}
