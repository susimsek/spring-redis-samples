package io.github.susimsek.springredissamples.ratelimiter;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.susimsek.springredissamples.spelresolver.SpelResolver;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@RequiredArgsConstructor
public class RateLimiterAspect implements Ordered {

    private final ProxyManager<String> proxyManager;
    private final SpelResolver spelResolver;
    private final RateLimiterProperties rateLimiterProperties;

    @Around("@annotation(rateLimiter)")
    public Object applyRateLimiter(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        String configName = rateLimiter.name();
        RateLimiterProperties.RateLimiterConfig configProps =
            rateLimiterProperties.getConfigs().getOrDefault(
                configName, rateLimiterProperties.getDefaultConfig());

        String keyExpression = rateLimiter.key();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String bucketKey;
        if (StringUtils.hasText(keyExpression)) {
            String resolvedKey = spelResolver.resolve(method, joinPoint.getArgs(), keyExpression);
            bucketKey = configName + ":" + resolvedKey;
        } else {
            bucketKey = configName + ":" + joinPoint.getSignature().toShortString();
        }

        BucketConfiguration configuration = BucketConfiguration.builder()
            .addLimit(Bandwidth.builder()
                .capacity(configProps.getLimitForPeriod())
                .refillGreedy(configProps.getLimitForPeriod(), configProps.getLimitRefreshPeriod())
                .build())
            .build();

        Bucket bucket = proxyManager.getProxy(bucketKey, () -> configuration);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            addRateLimitHeaders(configProps, probe);
            return joinPoint.proceed();
        } else {

            long waitTime = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
            long resetTimestamp = (System.currentTimeMillis() / 1000) + waitTime;

            if (StringUtils.hasText(rateLimiter.fallbackMethod())) {
                Object target = joinPoint.getTarget();
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method fallbackMethod = target.getClass().getMethod(
                    rateLimiter.fallbackMethod(), signature.getParameterTypes());
                return fallbackMethod.invoke(target, joinPoint.getArgs());
            }
            throw new RateLimitExceededException(
                rateLimiter.name(),
                "Rate limit exceeded: " + configName,
                waitTime,
                configProps.getLimitForPeriod(),
                probe.getRemainingTokens(),
                resetTimestamp
            );
        }
    }

    private void addRateLimitHeaders(RateLimiterProperties.RateLimiterConfig configProps,
                                     ConsumptionProbe probe) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null && attrs.getResponse() != null) {
            attrs.getResponse().addHeader(RateLimitHeaders.RATE_LIMIT_LIMIT,
                String.valueOf(configProps.getLimitForPeriod()));
            attrs.getResponse().addHeader(RateLimitHeaders.RATE_LIMIT_REMAINING,
                String.valueOf(probe.getRemainingTokens()));
        }
    }


    @Override
    public int getOrder() {
        return rateLimiterProperties.getAspectOrder();
    }
}
