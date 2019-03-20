package io.hychou.common.util;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.servererror.MultipartFileCannotGetBytesException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class TransformUtil {

    public static byte[] getBytesFrom(MultipartFile multipartFile) throws ServiceException {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new MultipartFileCannotGetBytesException("Fail to transform multipartFile into byte array", e);
        }
    }
}
