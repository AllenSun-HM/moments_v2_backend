package com.allen.moments.v2.aop;

import com.alibaba.fastjson.JSONObject;
import com.allen.moments.v2.utils.ApplicationException;
import com.allen.moments.v2.utils.JsonResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @AfterThrowing(pointcut = "execution(public * com.allen.moments.v2.api.*.*(..))", throwing="e")
    public JsonResult<?> doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        try {
            logger.error("------->Error Class:" + e.getClass().getName());
            logger.error("------->Error msg:" + e.getMessage());
            logger.error("------->Error method:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            Object[] arguments = joinPoint.getArgs();
            if (arguments !=  null && arguments.length > 0) {
                for ( int i = 0; i < arguments.length; i++) {
                    logger.error("------->args[" + i + "]: " +  JSONObject.toJSONString(arguments[i]));
                }
            }
            if (e.getClass() == ApplicationException.class) { // customized exception with err_no and message
                return JsonResult.failure(((ApplicationException) e).getErrNo(), e.getMessage());
            }
            return JsonResult.unknownFailure();
        }  catch (Exception ex) {
            logger.error("------->exception occured!");
            logger.error("------->exception message:{}", ex.getMessage());
            return JsonResult.unknownFailure();
        }

    }
}
