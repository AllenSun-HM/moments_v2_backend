package com.allen.moments.v2.aop;

import com.alibaba.fastjson.JSONObject;
import com.allen.moments.v2.utils.JsonResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


    @Aspect
    @Component
    public class LoggingHandler {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        /**
         * 在方法调用之前，打印入参
         */
        @Before(value = "execution(public * com.allen.moments.v2.api.*.*(..))")
        public void before(JoinPoint joinPoint) {
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            StringBuilder params = new StringBuilder();
            for (Object arg : args) {
                params.append(arg).append(" ");
            }
            logger.info(className + "的" + methodName + "入参为：" + params.toString());
        }

        /**
         * 返回之后，打印出参
         */
        @AfterReturning(value = "execution(public * com.allen.moments.v2.api.*.*(..))", returning = "returnVal")
        public void afterReturning(JoinPoint joinPoint, Object returnVal) {
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            logger.info(className + "的" + methodName + "结果为：" + returnVal.toString());
        }

    }

