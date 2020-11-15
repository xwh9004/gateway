package com.example.gateway.outbound.mock;

import com.example.gateway.outbound.Invoker;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.OK;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 16:19 on 2020/11/4
 * @version V0.1
 * @classNmae MockClient
 */
@Slf4j
public class MockClientInvoker implements Invoker {
    @Override
    public FullHttpResponse invoke(FullHttpRequest fullRequest, ChannelHandlerContext ctx) throws Exception {
        FullHttpResponse response = null;
        String value = "hello,this is mock!";
        response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(value.getBytes("UTF-8")));
        response.headers().set("Content-Type", "application/json");
        response.headers().setInt("Content-Length", response.content().readableBytes());
        return response;
    }
}
