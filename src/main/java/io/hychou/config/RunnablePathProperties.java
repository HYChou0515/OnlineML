package io.hychou.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:service.properties"})
@ConfigurationProperties(prefix = "path.runnable")
public class RunnablePathProperties {
    @Getter
    @Setter
    private String baseWorkingDir;
    @Getter
    @Setter
    private String anacondaEnvBaseDir;
}