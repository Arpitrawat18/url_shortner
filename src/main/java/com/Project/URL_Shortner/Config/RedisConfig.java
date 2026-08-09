package com.Project.URL_Shortner.Config;

import com.Project.URL_Shortner.Entities.UrlEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UrlEntity> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, UrlEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }
}
