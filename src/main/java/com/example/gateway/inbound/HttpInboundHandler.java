package com.example.gateway.inbound;

import com.example.gateway.filter.HttpRequestFilter;
import com.example.gateway.filter.HttpRequestTraceFilter;
import com.example.gateway.outbound.Invoker;
import com.example.gateway.outbound.httpclient4.HttpClientInvoker;
import com.example.gateway.outbound.netty4.NettyClientInvoker;
import com.example.gateway.outbound.okhttp.OkHttpClientInvoker;
import com.example.gateway.router.HttpEndpointRouter;
import com.example.gateway.router.RandomHttpEndpointRouter;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
@Slf4j
@Component
@ChannelHandler.Sharable
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
    @Value("${gateway.server.context-path}")
    private  String contextPath;
    @Autowired
    private HttpEndpointRouter router;

    @Autowired
    private Invoker invoker ;

    private List<HttpRequestFilter> filters = new ArrayList<HttpRequestFilter>();

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public HttpInboundHandler() {
        filters.add(new HttpRequestTraceFilter());
    }


    public void addFilter(HttpRequestFilter filter){
        filters.add(filter);
    }


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
    private void handleRequest(final FullHttpRequest fullRequest, ChannelHandlerContext ctx) throws Exception {
        String url = fullRequest.uri();
        if(!url.startsWith(contextPath)){
           return ;
        }
        filters.stream().forEach(filter -> filter.filter(fullRequest,ctx));
        String backendUri =router.route(url);
        fullRequest.setUri(backendUri);
        FullHttpResponse response =invoker.invoke(fullRequest, ctx);
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

}
