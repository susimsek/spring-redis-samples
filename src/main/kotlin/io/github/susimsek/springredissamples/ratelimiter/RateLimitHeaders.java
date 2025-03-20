package io.github.susimsek.springredissamples.ratelimiter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RateLimitHeaders {
    public static final String RETRY_AFTER = "Retry-After";
    public static final String RATE_LIMIT_RESET = "X-RateLimit-Reset";
    public static final String RATE_LIMIT_LIMIT = "X-RateLimit-Limit";
    public static final String RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
}
