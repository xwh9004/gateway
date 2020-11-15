package com.example.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.UUID;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 10:53 on 2020/11/3
 * @version V0.1
 * @classNmae HttpRequestTraceFilter
 */
public class HttpRequestTraceFilter implements HttpRequestFilter{
    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {

        HttpHeaders headers = fullRequest.headers();
        headers.add("globalSeqNo", UUID.randomUUID());

    }
}
