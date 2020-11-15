package com.example.gateway.outbound.netty4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 11:12 on 2020/11/4
 * @version V0.1
 * @classNmae NettyHttpOutboundHandler
 */
@Slf4j
public class NettyHttpOutboundHandler extends ChannelOutboundHandlerAdapter {
    private FullHttpRequest request;

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        log.info("NettyHttpOutboundHandler read,ctx="+ctx);
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        log.info("NettyHttpOutboundHandler write,ctx="+ctx);
        super.write(ctx, msg, promise);
    }
}
