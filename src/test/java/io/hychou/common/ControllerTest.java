package io.hychou.common;

import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public abstract class ControllerTest {
    protected MockMultipartHttpServletRequestBuilder putMultipart(String urlTemplate, Object... uriVars) {
        MockMultipartHttpServletRequestBuilder builder  = MockMvcRequestBuilders.multipart(urlTemplate, uriVars);
        builder.with(request -> {
            request.setMethod(HttpMethod.PUT.toString());
            return request;
        });
        return builder;
    }
}
