package io.github.susimsek.springredissamples.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.susimsek.springredissamples.ratelimiter.RateLimiterAspect;
import io.github.susimsek.springredissamples.ratelimiter.RateLimiterProperties;
import io.github.susimsek.springredissamples.spelresolver.SpelResolver;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RateLimiterProperties.class)
public class RateLimiterConfig {

    private final RateLimiterProperties rateLimiterProperties;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedisClient.class)
    public RedisClient redisClient(LettuceConnectionFactory connectionFactory) {
        return (RedisClient) connectionFactory.getNativeClient();
    }

    @Bean
    public ProxyManager<String> lettuceBasedProxyManager(RedisClient redisClient) {
        StatefulRedisConnection<String, byte[]> redisConnection = redisClient
            .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        return Bucket4jLettuce.casBasedBuilder(redisConnection)
            .expirationAfterWrite(ExpirationAfterWriteStrategy
                .basedOnTimeForRefillingBucketUpToMax(rateLimiterProperties.getBucketExpireDuration()))
            .build();
    }

    @Bean
    public RateLimiterAspect rateLimiterAspect(ProxyManager<String> proxyManager,
                                               SpelResolver spelResolver) {
        return new RateLimiterAspect(proxyManager, spelResolver, rateLimiterProperties);
    }
}
