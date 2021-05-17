package com.allen.moments.v2;

import com.allen.moments.v2.redis.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MomentsApplicationTests {
    @Autowired
    RedisUtil redisUtil;

    @Test
    void contextLoads() {
    }

    @Test
    void testRedisConnection() {
        redisUtil.set("abc" , 123);
    }
}
