package io.github.susimsek.springredissamples.ratelimiter;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {

    private final String rateLimiterName;
    private final long waitTime;
    private final long limitForPeriod;
    private final long availablePermissions;
    private final long resetTimestamp;

    public RateLimitExceededException(String rateLimiterName, String message, long waitTime,
                                      long limitForPeriod, long availablePermissions,
                                      long resetTimestamp) {
        super(message);
        this.rateLimiterName = rateLimiterName;
        this.waitTime = waitTime;
        this.limitForPeriod = limitForPeriod;
        this.availablePermissions = availablePermissions;
        this.resetTimestamp = resetTimestamp;
    }
}
