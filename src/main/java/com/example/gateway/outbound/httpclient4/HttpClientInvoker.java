package com.example.gateway.outbound.httpclient4;

import com.example.gateway.outbound.Invoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 14:25 on 2020/11/4
 * @version V0.1
 * @classNmae HttpProxyInvoker
 */
public class HttpClientInvoker implements Invoker {

    private HttpOutboundHandler httpOutboundHandler = new HttpOutboundHandler();


    @Override
    public FullHttpResponse invoke(FullHttpRequest fullRequest, ChannelHandlerContext ctx) throws Exception {
        String uri = fullRequest.uri();
        httpOutboundHandler.handle(fullRequest,ctx);
        return null;
    }

    @Override
    public FullHttpResponse invoke(FullHttpRequest fullRequest) throws Exception {
        return null;
    }
}
