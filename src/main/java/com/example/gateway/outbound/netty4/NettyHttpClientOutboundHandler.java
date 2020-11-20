package com.example.gateway.outbound.netty4;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.OK;
@ChannelHandler.Sharable
@Slf4j
public class NettyHttpClientOutboundHandler  extends ChannelInboundHandlerAdapter {

    private FullHttpRequest fullRequest;

    private ChannelHandlerContext serverCtx;


    public void setFullRequest(FullHttpRequest fullRequest) {
        this.fullRequest = fullRequest;
    }

    public void setServerCtx(ChannelHandlerContext serverCtx) {
        this.serverCtx = serverCtx;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("NettyHttpClient channelActive");
        super.channelActive(ctx);
//        ctx.channel().writeAndFlush(fullRequest);
    }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("NettyHttpClientOutboundHandler channelReadComplete "+ctx.toString());
        super.channelReadComplete(ctx);
//        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        
        log.info("channelRead ...");
        if(msg instanceof FullHttpResponse){
            FullHttpResponse response = (FullHttpResponse)msg;
            log.info("globalSeqNo="+response.headers().get("globalSeqNo"));
            try {
                handleResponse(fullRequest, serverCtx, response);

            }catch (Exception e){

               e.printStackTrace();
            }
        }
    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final FullHttpResponse endpointResponse) throws Exception {
        FullHttpResponse response = null;
        try {
            int length =endpointResponse.content().readableBytes();
            byte[] body =new byte[length];
            endpointResponse.content().readBytes(body);
            log.info("endpointResponse ={}",new String(body));
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            HttpHeaders headerOri = endpointResponse.headers();
            response.headers().add(headerOri);
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", length);

        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }

    }


}