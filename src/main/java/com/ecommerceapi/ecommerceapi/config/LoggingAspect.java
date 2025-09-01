package com.ecommerceapi.ecommerceapi.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    private static String beforeString = "[ entering < {} > ]";

    private static String beforeWithParamsString = "[ entering < {} > with params {} ]";

    private static String afterThrowing = "[ exception thrown < {} > exception message {} with params {} ]";

    private static String afterReturning = "[ leaving < {} > returning {} ]";

    private static String afterReturningVoid = "[ leaving < {} > ]";

    /**
     * Logs method entry of any class if the
     * logging level of class logger is DEBUG and the method is annotated
     * with @Log.
     *
     * @param joinPoint
     */

    @Before(value = "execution(* *(..)) && @annotation(com.ecommerceapi.ecommerceapi.config.Log)", argNames = "joinPoint")
    public void before(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Logger logger = getLogger(clazz);
        if (logger.isDebugEnabled()) {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                logger.debug(beforeString, methodName);
            } else {
                logger.debug(beforeWithParamsString, methodName, constructArgumentsString(args));
            }
        }
    }

    /**
     * Logs a successful method return using the class logger if the logging level of
     * class logger is DEBUG and the method is annotated with @Log.
     *
     * @param joinPoint
     * @param returnValue
     */
    @AfterReturning(value = "(execution(* *(..)) && @annotation(com.ecommerceapi.ecommerceapi.config.Log))", returning = "returnValue", argNames = "joinPoint, returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {
        Class<?> clazz = joinPoint.getTarget().getClass();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        Class<?> returnType = methodSignature.getReturnType();
        Logger logger = getLogger(clazz);

        if (logger.isDebugEnabled()) {
            if (Void.TYPE.equals(returnType)) {
                logger.debug(afterReturningVoid, methodName);
            } else {
                logger.debug(afterReturning, methodName, String.valueOf(returnValue));
            }
        }
    }

    /**
     * Logs exceptional method return from a SERVICE class if the logging level
     * of class logger is DEBUG.
     *
     * @param joinPoint
     * @param throwable
     */
    @AfterThrowing(value = "execution(public * com.ecommerceapi.ecommerceapi..*.*(..))", throwing = "throwable", argNames = "joinPoint, throwable")
    public void afterThrowing(JoinPoint joinPoint, Throwable throwable) {

        Class<? extends Object> clazz = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();

        Logger logger = getLogger(clazz);
        if (logger.isDebugEnabled()) {
            logger.debug(afterThrowing, methodName, throwable.getMessage(),
                    constructArgumentsString(joinPoint.getArgs()));
        }
    }

    private String constructArgumentsString(Object... arguments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : arguments) {
            if (object != null) {
                stringBuilder.append(object.toString());
                stringBuilder.append(",");
            }
        }

        return stringBuilder.length() > 0 ? stringBuilder.substring(0, stringBuilder.length() - 1) : "";
    }

    /**
     * Gets the logger of the class
     *
     * @param clazz class
     * @return the logger
     */
    private Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
