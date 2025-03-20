package io.github.susimsek.springredissamples.ratelimiter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {

    private Map<String, RateLimiterConfig> configs = new HashMap<>();
    private RateLimiterConfig defaultConfig = new RateLimiterConfig(100, Duration.ofSeconds(60));

    private Duration bucketExpireDuration = Duration.ofMinutes(1);

    private int aspectOrder = Ordered.LOWEST_PRECEDENCE - 2;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RateLimiterConfig {
        private long limitForPeriod;
        private Duration limitRefreshPeriod;
    }
}
