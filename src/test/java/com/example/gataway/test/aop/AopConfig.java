package com.example.gataway.test.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 11:09 on 2020/11/20
 * @version V0.1
 * @classNmae RouterAopConfig
 */
@Slf4j
@Aspect
@Component
public class AopConfig {

    @Around("execution(* com.example.gataway.test.service.TestService.*(..))")
    public Object router(ProceedingJoinPoint joinPoint)throws Throwable {
        StopWatch watch = new StopWatch();
        log.info("开始执行方法{} 参数{} ",joinPoint.getSignature().toShortString(),joinPoint.getArgs());
        watch.start();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("执行方法报错 {}",throwable.getMessage());
            throw throwable;
        }
        watch.stop();
        log.info("执行方法{} 结束参数{} 耗时：{}ms ",joinPoint.getSignature().toLongString(),joinPoint.getArgs(),watch.getTotalTimeMillis());
        return result;
    }
}
