package com.example.gateway.aop;

import com.example.gateway.filter.HttpRequestFilter;
import com.example.gateway.filter.HttpRequestTraceFilter;
import com.example.gateway.router.HttpEndpointRouter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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
@Order(1)
@Component
public class RouterAopConfig {

    @Autowired
    private HttpEndpointRouter router;

    /**
     * 前置后置通知不能使用 ProceedingJoinPoint
     * @param joinPoint
     * @throws Throwable
     */
    @Before("execution( * com.example.gateway.outbound.Invoker.invoke(..))")
    public void router(JoinPoint joinPoint)throws Throwable {

        log.info("--------------执行请求路由-----------");
        Object result = null;
        try {
            Object[] args = joinPoint.getArgs();
            FullHttpRequest fullRequest = (FullHttpRequest) args[0];
//            ChannelHandlerContext ctx = (ChannelHandlerContext) args[1];
            String url = fullRequest.uri();
            String backendUri =router.route(url);
            fullRequest.setUri(backendUri);
//            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("执行方法报错 {}",throwable.getMessage());
            throw throwable;
        }
        log.info("-------------执行请求路由结束-----------");
    }
}
