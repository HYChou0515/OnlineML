package io.hychou.common;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MessageResponseEntity<T> extends HttpEntity<T> {

    public final static String HTTP_HEADER_STATUS_MESSAGE = "Status-Message";
    private final Object status;

    public MessageResponseEntity(MultiValueMap<String, String> headers, HttpStatus status) {
        this(null, headers, status);
    }

    public MessageResponseEntity(@Nullable T body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers);
        Assert.notNull(headers, "HttpHeaders must not be null");
        Assert.notEmpty(headers, "HttpHeaders must not be empty");
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status;
    }

    private MessageResponseEntity(@Nullable T body, MultiValueMap<String, String> headers, Object status) {
        super(body, headers);
        Assert.notNull(headers, "HttpHeaders must not be null");
        Assert.notEmpty(headers, "HttpHeaders must not be empty");
        Assert.notNull(status, "HttpStatus must not be null");
        this.status = status;
    }


    public HttpStatus getStatusCode() {
        if (this.status instanceof HttpStatus) {
            return (HttpStatus) this.status;
        }
        else {
            return HttpStatus.valueOf((Integer) this.status);
        }
    }

    public int getStatusCodeValue() {
        if (this.status instanceof HttpStatus) {
            return ((HttpStatus) this.status).value();
        }
        else {
            return (Integer) this.status;
        }
    }


    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        MessageResponseEntity<?> otherEntity = (MessageResponseEntity<?>) other;
        return ObjectUtils.nullSafeEquals(this.status, otherEntity.status);
    }

    @Override
    public int hashCode() {
        return (super.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.status));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        builder.append(this.status.toString());
        if (this.status instanceof HttpStatus) {
            builder.append(' ');
            builder.append(((HttpStatus) this.status).getReasonPhrase());
        }
        builder.append(',');
        T body = getBody();
        HttpHeaders headers = getHeaders();
        if (body != null) {
            builder.append(body);
            builder.append(',');
        }
        builder.append(headers);
        builder.append('>');
        return builder.toString();
    }


    // Static builder methods

    public static MessageResponseEntity.BodyBuilder status(HttpStatus status, String message) {
        Assert.notNull(status, "HttpStatus must not be null");
        Assert.notNull(message, "StatusMessage must not be null");
        return new MessageResponseEntity.DefaultBuilder(status, message);
    }

    public static MessageResponseEntity.BodyBuilder status(int status, String message) {
        return new MessageResponseEntity.DefaultBuilder(status, message);
    }

    public static MessageResponseEntity.BodyBuilder ok(String message) {
        return status(HttpStatus.OK, message);
    }

    public static <T> MessageResponseEntity<T> ok(T body, String message) {
        MessageResponseEntity.BodyBuilder builder = ok(message);
        return builder.body(body);
    }

    public static MessageResponseEntity.BodyBuilder created(URI location, String message) {
        MessageResponseEntity.BodyBuilder builder = status(HttpStatus.CREATED, message);
        return builder.location(location);
    }

    public static MessageResponseEntity.BodyBuilder accepted(String message) {
        return status(HttpStatus.ACCEPTED, message);
    }

    public static MessageResponseEntity.HeadersBuilder<?> noContent(String message) {
        return status(HttpStatus.NO_CONTENT, message);
    }

    public static MessageResponseEntity.BodyBuilder badRequest(String message) {
        return status(HttpStatus.BAD_REQUEST, message);
    }

    public static MessageResponseEntity.HeadersBuilder<?> notFound(String message) {
        return status(HttpStatus.NOT_FOUND, message);
    }

    public static MessageResponseEntity.BodyBuilder unprocessableEntity(String message) {
        return status(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    public interface HeadersBuilder<B extends MessageResponseEntity.HeadersBuilder<B>> {

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


    public interface BodyBuilder extends MessageResponseEntity.HeadersBuilder<MessageResponseEntity.BodyBuilder> {

        MessageResponseEntity.BodyBuilder contentLength(long contentLength);

        MessageResponseEntity.BodyBuilder contentType(MediaType contentType);

        <T> MessageResponseEntity<T> multipartFormData(String filename, @Nullable T body);

        <T> MessageResponseEntity<T> body(@Nullable T body);
    }


    private static class DefaultBuilder implements MessageResponseEntity.BodyBuilder {

        private final Object statusCode;

        private final HttpHeaders headers = new HttpHeaders();

        public DefaultBuilder(Object statusCode, String message) {
            this.statusCode = statusCode;
            this.headers.add(HTTP_HEADER_STATUS_MESSAGE, message);
        }

        @Override
        public MessageResponseEntity.BodyBuilder header(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        @Override
        public MessageResponseEntity.BodyBuilder headers(@Nullable HttpHeaders headers) {
            if (headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        @Override
        public MessageResponseEntity.BodyBuilder allow(HttpMethod... allowedMethods) {
            this.headers.setAllow(new LinkedHashSet<>(Arrays.asList(allowedMethods)));
            return this;
        }

        @Override
        public MessageResponseEntity.BodyBuilder contentLength(long contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        @Override
        public MessageResponseEntity.BodyBuilder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
            return this;
        }

        @Override
        public MessageResponseEntity.BodyBuilder eTag(String etag) {
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
        public MessageResponseEntity.BodyBuilder lastModified(long date) {
            this.headers.setLastModified(date);
            return this;
        }

        @Override
        public MessageResponseEntity.BodyBuilder location(URI location) {
            this.headers.setLocation(location);
            return this;
        }

        @Override
        public MessageResponseEntity.BodyBuilder cacheControl(CacheControl cacheControl) {
            String ccValue = cacheControl.getHeaderValue();
            if (ccValue != null) {
                this.headers.setCacheControl(cacheControl.getHeaderValue());
            }
            return this;
        }

        @Override
        public MessageResponseEntity.BodyBuilder varyBy(String... requestHeaders) {
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
            return new MessageResponseEntity<>(body, this.headers, this.statusCode);
        }
    }

}
