package com.example.gateway.inbound;

import com.example.gateway.config.RabbitMQConfig;
import com.example.gateway.jms.MessageProducer;
import com.rabbitmq.client.Channel;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspResponseStatuses.OK;

@Slf4j
@Component
@ChannelHandler.Sharable
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
    @Value("${gateway.server.context-path}")
    private  String contextPath;
    @Autowired
    private  MessageProducer messageProducer;

    private ChannelHandlerContext ctx;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        //可以启动一个代理客户端
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            long startTime = System.currentTimeMillis();

            // http://localhost:8888/api/{serviceName}/xxx
            final FullHttpRequest fullRequest = (FullHttpRequest) msg;
            String url = fullRequest.uri();
            if(!url.startsWith(contextPath)){
                return ;
            }
            handleRequest(fullRequest,ctx);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * handle request
     */
    public void handleRequest(final FullHttpRequest fullRequest, ChannelHandlerContext ctx) throws Exception {

          messageProducer.produce(fullRequest);
          this.ctx = ctx;

    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final FullHttpResponse response) throws Exception {

        try {
            response.headers().set("Content-Type", "application/json");
        } catch (Exception e) {
            e.printStackTrace();
            FullHttpResponse defaultResponse = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
            ctx.flush();
            //ctx.close();
        }

    }


    @RabbitListener(queues = RabbitMQConfig.GATEWAY_RESPONSE)
    public void consume(byte[] body, Message message, Channel channel) throws IOException {

        FullHttpResponse response = null;
        response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
//        HttpHeaders headerOri = endpointResponse.headers();
//        response.headers().add(headerOri);
        response.headers().set("Content-Type", "application/json");
        response.headers().setInt("Content-Length", body.length);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        ctx.writeAndFlush(response);

    }
}
