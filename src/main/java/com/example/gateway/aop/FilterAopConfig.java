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

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Description:</b>
 * 定制切面时  切面的方法如果是内嵌调用则切面不起作用比如
 * <code>
 *  public class Service{
 *
 *
 *      public void test1{
 *          test2();
 *          System.out.println("...")
 *      }
 *
 *      public void test2{
 *          System.out.println("...")
 *       }
 *  }
 *
 * AOP   execution( * Service.test2(..))
 *
 * 如果指定 test1方法调用test2,那么test2的AOP不生效
 *
 *
 *
 * </code>
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 11:09 on 2020/11/20
 * @version V0.1
 * @classNmae RouterAopConfig
 */
@Slf4j
@Aspect
@Order(2)  //指定切面的执行顺序
@Component
public class FilterAopConfig {



    private List<HttpRequestFilter> filters = new ArrayList<HttpRequestFilter>();
    @PostConstruct
    public void init(){
        filters.add(new HttpRequestTraceFilter());
    }

    @Before("execution( * com.example.gateway.outbound.Invoker.invoke(..))")
    public void filter(JoinPoint joinPoint)throws Throwable {

        log.info("--------------执行请求过滤器 开始------------");
        Object result = null;
        try {
            Object[] args = joinPoint.getArgs();
            FullHttpRequest fullRequest = (FullHttpRequest) args[0];
            ChannelHandlerContext ctx = (ChannelHandlerContext) args[1];
            filters.stream().forEach(filter -> filter.filter(fullRequest,ctx));
        } catch (Throwable throwable) {
            log.error("执行方法报错 {}",throwable.getMessage());
            throw throwable;
        }
        log.info("--------------执行请求过滤器 结束------------");

    }
}
