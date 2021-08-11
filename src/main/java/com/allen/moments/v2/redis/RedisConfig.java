package com.allen.moments.v2.redis;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;


@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig {
    @Autowired
    RedisProperties redisProperties;

    @Bean
    public GenericObjectPoolConfig poolConfig() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        config.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        config.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        config.setMaxWaitMillis(redisProperties.getLettuce().getPool().getMaxWait().toMillis());
        return config;
    }
    
    /**
     * @Description:  add sentinel config
     */
    @Bean
    public RedisClusterConfiguration configuration() {
        RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
//      redisConfig.setPassword(RedisPassword.of(redisConfigThree.getPassword()));
        if(redisProperties.getSentinel().getNodes()!=null) {
            ArrayList<RedisNode> nodes =new ArrayList<RedisNode>();
            for(String sen : redisProperties.getCluster().getNodes()) {
                String[] hostWithPort = sen.split(":");
                nodes.add(new RedisNode(hostWithPort[0], Integer.parseInt(hostWithPort[1])));
            }
            redisConfig.setClusterNodes(nodes);
        }
        return redisConfig;
    }


    @Bean("RedisConnectionFactory")
    public LettuceConnectionFactory RedisConnectionFactory(@Qualifier("poolConfig") GenericObjectPoolConfig config,
                                                             @Qualifier("configuration") RedisClusterConfiguration redisConfig) {//注意传入的对象名和类型RedisSentinelConfiguration
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }


    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("RedisConnectionFactory") LettuceConnectionFactory factory) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(factory);
            RedisSerializer<String> serializer = new StringRedisSerializer();
            redisTemplate.setKeySerializer(serializer);
            redisTemplate.setHashKeySerializer(serializer);
            redisTemplate.setValueSerializer(serializer);
            redisTemplate.setHashValueSerializer(serializer);
            redisTemplate.afterPropertiesSet();
            return redisTemplate;
    }
}