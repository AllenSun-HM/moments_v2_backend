package com.allen.moments.v2.aop;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;


    @Aspect
    @Component
    public class LoggingHandler {

        private final Logger logger =  LogManager.getLogger(this.getClass());

        /**
         *
         */
        @Before(value = "execution(public * com.allen.moments.v2.api.*.*(..))")
        public void before(JoinPoint joinPoint) {
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            logger.info("class " + className + "'s " + methodName + " has parameter(s)：" + JSONObject.toJSONString(args));
        }

        /**
         * after method return, print returned value
         */
        @AfterReturning(value = "execution(public * com.allen.moments.v2.api.*.*(..))", returning = "returnVal")
        public void afterReturning(JoinPoint joinPoint, Object returnVal) {
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            logger.info("class " + className + "'s " + methodName + " returns: " + returnVal.toString());
        }

    }

