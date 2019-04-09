package io.hychou.common;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MessageResponseEntity<T> extends ResponseEntity<T> {

    public static final String HTTP_HEADER_STATUS_MESSAGE = "Status-Message";

    public MessageResponseEntity(MultiValueMap<String, String> headers, HttpStatus status) {
        this(null, headers, status);
    }

    public MessageResponseEntity(@Nullable T body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers, status);
        Assert.notNull(headers, "HttpHeaders must not be null");
        Assert.notEmpty(headers, "HttpHeaders must not be empty");
        Assert.notNull(status, "HttpStatus must not be null");
    }

    private static HttpStatus toStatusCode(Object status) {
        if (status instanceof HttpStatus) {
            return (HttpStatus) status;
        }
        else {
            return HttpStatus.valueOf((Integer) status);
        }
    }

    // Static builder methods

    public static BodyBuilder status(HttpStatus status, String message) {
        Assert.notNull(status, "HttpStatus must not be null");
        Assert.notNull(message, "StatusMessage must not be null");
        return new DefaultBuilder(status, message);
    }

    public static BodyBuilder status(int status, String message) {
        return new DefaultBuilder(status, message);
    }

    public static BodyBuilder ok(String message) {
        return status(HttpStatus.OK, message);
    }

    public static <T> MessageResponseEntity<T> ok(T body, String message) {
        BodyBuilder builder = ok(message);
        return builder.body(body);
    }

    public static BodyBuilder created(URI location, String message) {
        BodyBuilder builder = status(HttpStatus.CREATED, message);
        return builder.location(location);
    }

    public static BodyBuilder accepted(String message) {
        return status(HttpStatus.ACCEPTED, message);
    }

    public static MessageResponseEntity.HeadersBuilder<?> noContent(String message) {
        return status(HttpStatus.NO_CONTENT, message);
    }

    public static BodyBuilder badRequest(String message) {
        return status(HttpStatus.BAD_REQUEST, message);
    }

    public static MessageResponseEntity.HeadersBuilder<?> notFound(String message) {
        return status(HttpStatus.NOT_FOUND, message);
    }

    public static BodyBuilder unprocessableEntity(String message) {
        return status(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    public interface HeadersBuilder<B extends HeadersBuilder<B>> {

        /**
         * Add the given, single header value under the given name.
         * @param headerName the header name
         * @param headerValues the header value(s)
         * @return this builder
         * @see HttpHeaders#add(String, String)
         */
        B header(String headerName, String... headerValues);

        B headers(@Nullable HttpHeaders headers);

        B allow(HttpMethod... allowedMethods);

        B eTag(String etag);

        B lastModified(long lastModified);

        B location(URI location);

        B cacheControl(CacheControl cacheControl);

        B varyBy(String... requestHeaders);

        <T> MessageResponseEntity<T> build();
    }

    public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {

        BodyBuilder contentLength(long contentLength);

        BodyBuilder contentType(MediaType contentType);

        <T> MessageResponseEntity<T> multipartFormData(String filename, T body);

        <T> MessageResponseEntity<T> body(@Nullable T body);
    }

    private static class DefaultBuilder implements BodyBuilder {

        private final Object statusCode;

        private final HttpHeaders headers = new HttpHeaders();

        public DefaultBuilder(Object statusCode, String message) {
            this.statusCode = statusCode;
            this.headers.add(HTTP_HEADER_STATUS_MESSAGE, message);
        }

        @Override
        public BodyBuilder header(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        @Override
        public BodyBuilder headers(@Nullable HttpHeaders headers) {
            if (headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        @Override
        public BodyBuilder allow(HttpMethod... allowedMethods) {
            this.headers.setAllow(new LinkedHashSet<>(Arrays.asList(allowedMethods)));
            return this;
        }

        @Override
        public BodyBuilder contentLength(long contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        @Override
        public BodyBuilder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
            return this;
        }

        @Override
        public BodyBuilder eTag(String etag) {
            if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
                etag = "\"" + etag;
            }
            if (!etag.endsWith("\"")) {
                etag = etag + "\"";
            }
            this.headers.setETag(etag);
            return this;
        }

        @Override
        public BodyBuilder lastModified(long date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public BodyBuilder location(URI location) {
            this.headers.setLocation(location);
            return this;
        }

        @Override
        public BodyBuilder cacheControl(CacheControl cacheControl) {
            String ccValue = cacheControl.getHeaderValue();
            if (ccValue != null) {
                this.headers.setCacheControl(cacheControl.getHeaderValue());
            }
            return this;
        }

        @Override
        public BodyBuilder varyBy(String... requestHeaders) {
            this.headers.setVary(Arrays.asList(requestHeaders));
            return this;
        }

        @Override
        public <T> MessageResponseEntity<T> build() {
            return body(null);
        }

        @Override
        public <T> MessageResponseEntity<T> multipartFormData(String filename, T body) {
            return this.contentType(MediaType.MULTIPART_FORM_DATA)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\""+filename+"\"")
                    .body(body);
        }

        @Override
        public <T> MessageResponseEntity<T> body(@Nullable T body) {
            return new MessageResponseEntity<>(body, this.headers, toStatusCode(this.statusCode));
        }
    }

}
