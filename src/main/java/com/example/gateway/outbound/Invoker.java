package com.example.gateway.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 14:22 on 2020/11/4
 * @version V0.1
 * @classNmae IClient
 */
public interface Invoker {

    FullHttpResponse invoke(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) throws Exception;
}
