package io.github.susimsek.springredissamples.config;

import static org.springdoc.core.utils.Constants.ALL_PATTERN;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

    @Bean
    @Profile("!prod")
    public GroupedOpenApi actuatorApi(
        OpenApiCustomizer actuatorOpenApiCustomizer,
        OperationCustomizer actuatorCustomizer,
        WebEndpointProperties endpointProperties,
        @Value("${springdoc.version}") String appVersion){
        return GroupedOpenApi.builder()
            .group("actuator")
            .pathsToMatch(endpointProperties.getBasePath() + ALL_PATTERN)
            .addOpenApiCustomizer(actuatorOpenApiCustomizer)
            .addOperationCustomizer(actuatorCustomizer)
            .pathsToExclude("/health/*")
            .addOpenApiCustomizer(openApi -> openApi.info(new io.swagger.v3.oas.models.info.Info()
                .title("Actuator API")
                .version(appVersion)))
            .build();
    }

    @Bean
    public GroupedOpenApi redisBreakerGroup(@Value("${springdoc.version}") String appVersion) {
        return GroupedOpenApi.builder().group("redis")
            .addOpenApiCustomizer(openApi -> openApi.info(new io.swagger.v3.oas.models.info.Info()
                .title("Redis Demo API").version(appVersion)))
            .pathsToMatch("/api/v1/redis/**")
            .build();
    }
}
