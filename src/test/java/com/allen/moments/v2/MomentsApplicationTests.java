package com.allen.moments.v2;

import com.allen.moments.v2.redis.RedisUtil;
import com.allen.moments.v2.utils.ThreadPoolManager;
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

    @Test
    void testThreadPool() {
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalI);
                }
            };
            ThreadPoolManager.getInstance().execute(r);
        }

    }
}
