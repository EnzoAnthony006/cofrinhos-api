package com.enzoanthonydev.cofrinhos.cofrinhos_api.infrastructure;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CacheNames;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoResumo;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Duration TTL_PADRAO = Duration.ofMinutes(5);
    private static final Duration TTL_COFRINHOS = Duration.ofMinutes(10);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration configPadrao = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(TTL_PADRAO)
                .disableCachingNullValues();

        RedisCacheConfiguration configCofrinhos = configPadrao
                .entryTtl(TTL_COFRINHOS)
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new JacksonJsonRedisSerializer<>(CofrinhoResumo.class)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(configPadrao)
                .withInitialCacheConfigurations(Map.of(CacheNames.COFRINHOS, configCofrinhos))
                .transactionAware()
                .build();
    }
}
