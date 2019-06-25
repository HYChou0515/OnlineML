package io.hychou.data.util;

import io.hychou.common.exception.IllegalArgumentException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class DataUtilsTest {
    @Test
    public void checkData_givenValidData_shouldNotThrowAnything() throws Exception {
        File heartScale = ResourceUtils.getFile("classpath:data/heart_scale");
        DataUtils.checkData(Files.readAllBytes(heartScale.toPath()));
    }

    @Test
    public void checkData_givenIndexNotAscending_shouldThrowException() throws Exception {
        File data = ResourceUtils.getFile("classpath:data/bad/index_not_ascending");
        assertThrows(IllegalArgumentException.class, () -> DataUtils.checkData(Files.readAllBytes(data.toPath())));
    }

    @Test
    public void checkData_givenLabelNotNumber_shouldThrowException() throws Exception {
        File data = ResourceUtils.getFile("classpath:data/bad/label_is_not_a_number");
        assertThrows(IllegalArgumentException.class, () -> DataUtils.checkData(Files.readAllBytes(data.toPath())));
    }

    @Test
    public void checkData_givenMultiLabelNotNumber_shouldThrowException() throws Exception {
        File data = ResourceUtils.getFile("classpath:data/bad/multilabel_is_not_a_number");
        assertThrows(IllegalArgumentException.class, () -> DataUtils.checkData(Files.readAllBytes(data.toPath())));
    }

    @Test
    public void checkData_givenEmptyLines_shouldThrowException() throws Exception {
        File data = ResourceUtils.getFile("classpath:data/bad/empty_lines");
        assertThrows(IllegalArgumentException.class, () -> DataUtils.checkData(Files.readAllBytes(data.toPath())));
    }

    @Test
    public void checkData_givenFeatureNotNumber_shouldThrowException() throws Exception {
        File data = ResourceUtils.getFile("classpath:data/bad/feature_is_not_a_number");
        assertThrows(IllegalArgumentException.class, () -> DataUtils.checkData(Files.readAllBytes(data.toPath())));
    }

    @Test
    public void checkData_givenFeatureIsInf_shouldThrowException() throws Exception {
        File data = ResourceUtils.getFile("classpath:data/bad/feature_is_inf");
        assertThrows(IllegalArgumentException.class, () -> DataUtils.checkData(Files.readAllBytes(data.toPath())));
    }

    @Test
    public void checkData_givenNegativeIndex_shouldThrowException() throws Exception {
        File data = ResourceUtils.getFile("classpath:data/bad/negative_index");
        assertThrows(IllegalArgumentException.class, () -> DataUtils.checkData(Files.readAllBytes(data.toPath())));
    }
}
