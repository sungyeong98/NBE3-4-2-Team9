package com.backend.global.redis;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@TestConfiguration
public class TestRedisConfig {

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = Mockito.mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);

        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(valueOperations.get(Mockito.anyString())).thenReturn("mocked-token");
        Mockito.doNothing().when(valueOperations).set(Mockito.anyString(), Mockito.any());
        Mockito.doNothing().when(valueOperations).set(Mockito.anyString(), Mockito.any(), Mockito.anyLong(), Mockito.any());

        return redisTemplate;
    }

}
