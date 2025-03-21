package io.github.susimsek.springredissamples.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.susimsek.springredissamples.ratelimiter.RateLimiterAspect;
import io.github.susimsek.springredissamples.ratelimiter.RateLimiterProperties;
import io.github.susimsek.springredissamples.spelresolver.SpelResolver;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(Bucket4jLettuce.class)
@EnableConfigurationProperties(RateLimiterProperties.class)
public class RateLimiterConfig {

    private final RateLimiterProperties rateLimiterProperties;

    @Bean
    @ConditionalOnMissingBean(name = "lettuceBasedProxyManager")
    public ProxyManager<String> lettuceBasedProxyManager(LettuceConnectionFactory connectionFactory,
                                                         RedisProperties redisProperties) {
        AbstractRedisClient nativeClient = connectionFactory.getNativeClient();

        Bucket4jLettuce.LettuceBasedProxyManagerBuilder<String> builder;

        if (redisProperties.getCluster() != null) {
            RedisClusterClient redisClusterClient = (RedisClusterClient) nativeClient;
            StatefulRedisClusterConnection<String, byte[]> clusterConnection = redisClusterClient
                .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
            builder = Bucket4jLettuce.casBasedBuilder(clusterConnection);
        } else {
            RedisClient redisClient = (RedisClient) nativeClient;
            StatefulRedisConnection<String, byte[]> connection = redisClient
                .connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
            builder = Bucket4jLettuce.casBasedBuilder(connection);
        }

        return builder.expirationAfterWrite(
                ExpirationAfterWriteStrategy
                    .basedOnTimeForRefillingBucketUpToMax(rateLimiterProperties.getBucketExpireDuration()))
            .build();
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiterAspect.class)
    public RateLimiterAspect rateLimiterAspect(ProxyManager<String> proxyManager,
                                               SpelResolver spelResolver) {
        return new RateLimiterAspect(proxyManager, spelResolver, rateLimiterProperties);
    }
}
