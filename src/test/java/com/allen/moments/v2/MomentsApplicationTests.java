package com.allen.moments.v2;

import com.allen.moments.v2.dao.UserDao;
import com.allen.moments.v2.model.User;
import com.allen.moments.v2.redis.RedisUtil;
import com.allen.moments.v2.utils.ThreadPoolManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MomentsApplicationTests {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() {
    }


//    @Test
//    void testRedisConnection() {
//        redisUtil.set("abc" , 123);
//    }
//
//    @Test
//    void testThreadPool() {
//        for (int i = 0; i < 100; i++) {
//            int finalI = i;
//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println(finalI);
//                }
//            };
//            ThreadPoolManager.getInstance().execute(r);
//        }
//    }
//
//    @Test
//    void writeIntoDB() throws InterruptedException {
//        for (int i = 0; i < 500; i++) {
//            Thread.sleep(10);
//            User user = new User("test" + i, "test" + i + "@test.com", 100020 + i, (int) Math.floor(Math.random() * 20), Math.random() < 0.5 ? 1 : 2, "123456");
//            userDao.insert(user);
//        }
//    }

}
