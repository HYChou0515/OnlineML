package io.hychou.data.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import io.hychou.common.ControllerTest;
import io.hychou.data.entity.DataEntity;
import io.hychou.data.entity.DataInfo;
import io.hychou.data.service.DataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DataController.class)
public class DataControllerTest extends ControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    private DataService dataService;

    private DataEntity a9a;
    private DataEntity rcv1;
    private DataEntity a9aOther;
    private List<DataInfo> dataInfoList;

    // JacksonTester is auto built in setUp
    // JacksonTester.initFields();
    private JacksonTester<DataEntity> dataEntityJacksonTester;
    private JacksonTester<List<DataEntity>> dataEntityListJacksonTester;
    private JacksonTester<List<DataInfo>> dataInfoListJacksonTester;

    private class DataInfoEntity implements DataInfo {
        private String name;

        DataInfoEntity(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return null;
        }
    }

    @Before
    public void setUp() {
        JacksonTester.initFields(this, new ObjectMapper());

        a9a = new DataEntity();
        a9a.setName("a9a");
        a9a.setDataBytes("This is a9a".getBytes());

        rcv1 = new DataEntity();
        rcv1.setName("rcv1");
        rcv1.setDataBytes("This is rcv1".getBytes());

        a9aOther = new DataEntity();
        a9aOther.setName("a9aOther");
        a9aOther.setDataBytes("This is a9a".getBytes());

        dataInfoList = Arrays.asList(
                new DataInfoEntity(a9a.getName()),
                new DataInfoEntity(rcv1.getName()),
                new DataInfoEntity(a9aOther.getName())
        );
    }

    // =====================================================================
    // readAllDataInfo
    // =====================================================================

    @Test
    public void readAllDataInfo_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        given(dataService.listDataInfo()).willReturn(dataInfoList);

        // Act
        MockHttpServletResponse response = mvc.perform(
                get("/data/info").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(dataInfoListJacksonTester.write(dataInfoList).getJson(),
                        response.getContentAsString())
        );
    }

    // =====================================================================
    // readDataByName
    // =====================================================================

    @Test
    public void readDataByName_thenReturnProperResponseEntity() throws Exception {
        // Arrange
        given(dataService.readDataByName(a9a.getName())).willReturn(a9a);

        // Act
        MockHttpServletResponse response = mvc.perform(
                get("/data/"+a9a.getName()).accept(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
                () -> assertEquals(new String(a9a.getDataBytes()), response.getContentAsString()),
                () -> assertEquals("attachment; filename=\""+a9a.getName()+"\"", response.getHeader(HttpHeaders.CONTENT_DISPOSITION))
        );
    }

    // =====================================================================
    // createDataByName
    // =====================================================================

    @Test
    public void createData_thenReturnProperResponseEntity () throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9a.getDataBytes());
        given(dataService.createData(a9a)).willReturn(a9aOther);

        // Act
        MockHttpServletResponse response = mvc.perform(
                multipart("/data/"+a9a.getName()).file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> then(dataService).should(times(1)).createData(a9a)
        );
    }

    // =====================================================================
    // updateDataByName
    // =====================================================================

    @Test
    public void updateData_thenReturnProperResponseEntity () throws Exception {
        // Arrange
        MockMultipartFile multipartFile = new MockMultipartFile("blob", a9a.getDataBytes());
        given(dataService.updateData(a9a)).willReturn(a9aOther);

        // Act
        MockHttpServletResponse response = mvc.perform(
                putMultipart("/data/"+a9a.getName()).file(multipartFile))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> then(dataService).should().updateData(a9a)
        );
    }

    // =====================================================================
    // deleteDataByName
    // =====================================================================

    @Test
    public void deleteDataByName_thenReturnProperResponseEntity () throws Exception {
        // Arrange
        doNothing().when(dataService).deleteDataByName(a9a.getName());

        // Act
        MockHttpServletResponse response = mvc.perform(
                delete("/data/"+a9a.getName()))
                .andReturn().getResponse();

        // Assert
        assertAll("response",
                () -> assertEquals(response.getStatus(), HttpStatus.OK.value()),
                () -> then(dataService).should(times(1)).deleteDataByName(a9a.getName())
        );
    }
}
