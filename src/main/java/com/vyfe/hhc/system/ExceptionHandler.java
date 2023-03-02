package com.vyfe.hhc.system;

import com.vyfe.hhc.api.BaseResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * ExceptionHandler类.
 * <p>
 * User: chenyifei03
 * Date: 2023/2/16
 * Description:
 */
@Component
@Aspect
@Order(0)
public class ExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);
    
    @Around("execution(* com.vyfe.hhc..*.*(..))")
    public Object stargateMethodExceptionHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        Class returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType();
        try {
            return joinPoint.proceed();
        } catch (HhcException e) {
            if (BaseResponse.class.isAssignableFrom(returnType)) {
                return BaseResponse.withStatusAndInfoAndData(1, e.getMessage(), null);
            }
            LOGGER.error("meeting hhcException, msg:{}", e.getMessage());
            throw e;
        } catch (Throwable throwable) {
            if (BaseResponse.class.isAssignableFrom(returnType)) {
               LOGGER.error("ExceptionHandlerAspect catch Throwable.", throwable);
                return BaseResponse.withStatusAndInfoAndData(2, throwable.getMessage(), null);
            }
            throw throwable;
        }
    }
}
