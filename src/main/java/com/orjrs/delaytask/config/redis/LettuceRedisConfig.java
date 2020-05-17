package com.orjrs.delaytask.config.redis;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * RedisConfig
 *
 * @author orjrs
 * @date 2018-08-11 21:25
 */
@Configuration
@EnableCaching
public class LettuceRedisConfig extends CachingConfigurerSupport {
    @Bean(name = "lettuceConnectionFactory")
    @Primary
    public ReactiveRedisConnectionFactory connectionFactory() {
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .useSsl()
                .and()
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ZERO)
                .build();
        //return new LettuceConnectionFactory("localhost", 6379);
        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration("localhost", 6379), clientConfiguration);
    }


    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
    }

   /* @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate<>(factory);
    }*/


}
