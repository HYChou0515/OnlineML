package io.hychou.common.util;

import io.hychou.common.exception.service.ServiceException;
import io.hychou.common.exception.service.servererror.MultipartFileCannotGetBytesException;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransformUtil {

    public static byte[] getBytesFrom(MultipartFile multipartFile) throws ServiceException {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new MultipartFileCannotGetBytesException("Fail to transform multipartFile into byte array", e);
        }
    }

    public static List<Pair> mergeTwoListToListOfPairs(List listA, List listB) {
        assert Objects.nonNull(listA);
        assert Objects.nonNull(listB);
        assert listA.size() == listB.size() : "merged to lists must have same size";
        List<Pair> list = new ArrayList<>();
        for(int i = 0; i < listA.size(); i++) {
            list.add(Pair.of(listA.get(i), listB.get(i)));
        }
        return list;
    }
}
