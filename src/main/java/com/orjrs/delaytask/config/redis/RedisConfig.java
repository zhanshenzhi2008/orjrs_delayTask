package com.orjrs.delaytask.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;
import java.time.Duration;

/**
 * RedisConfig
 *
 * @author orjrs
 * @date 2018-08-11 21:25
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
    @Bean(name="lettuceConnectionFactory")
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

    /**
     * redis连接的基础设置
     *
     * @return JedisConnectionFactory
     */
    @Bean(name="jedisConnectionFactory")
    public JedisConnectionFactory redisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        return factory;
    }


    @Bean
    public RedisTemplate<?, ?> redisTemplate(@Qualifier("lettuceConnectionFactory") RedisConnectionFactory factory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);
            /*Map<String, Long> expires=new HashMap<String, Long>();
            expires.put("user", 6000L);
            expires.put("city", 600L);
            cacheManager.setExpires(expires);*/
        // 如果方法上有Long等非String类型的话，会报类型转换错误，故设置序列化工具
        setSerializer(redisTemplate);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;

    }

    /**
     * 序列化StringRedisTemplate
     *
     * @param redisTemplate redis模板
     */
    private void setSerializer(RedisTemplate<?, ?> redisTemplate) {
        Jackson2JsonRedisSerializer jackson2JsonSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setDateFormat(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"));// 处理日期格式
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonSerializer.setObjectMapper(om);
        redisTemplate.setKeySerializer(jackson2JsonSerializer);
        redisTemplate.setValueSerializer(jackson2JsonSerializer);
        redisTemplate.setHashKeySerializer(jackson2JsonSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonSerializer);
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        redisTemplate.setStringSerializer(redisSerializer);

        //key序列化方式;但是如果方法上有Long等非String类型的话，会报类型转换错误；
       /* RedisSerializer<String> redisSerializer = new StringRedisSerializer();//Long类型不可以会出现异常信息;
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);*/

        //JdkSerializationRedisSerializer序列化方式;
//        JdkSerializationRedisSerializer jdkRedisSerializer = new JdkSerializationRedisSerializer();
//        redisTemplate.setValueSerializer(jdkRedisSerializer);
//        redisTemplate.setHashValueSerializer(jdkRedisSerializer);

    }
}
