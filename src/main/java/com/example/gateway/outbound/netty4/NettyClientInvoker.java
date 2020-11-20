package com.example.gateway.outbound.netty4;//package io.github.kimmking.com.example.gateway.outbound;

import com.example.gateway.outbound.Invoker;
import com.example.gateway.outbound.httpclient4.NamedThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class NettyClientInvoker implements Invoker {

    private  EventLoopGroup workerGroup;

    private Bootstrap b = null;

    private Map<String,ChannelFuture> channelMap = new ConcurrentHashMap<String,ChannelFuture>();

    private NettyHttpClientOutboundHandler outboundHandler;

    private ExecutorService proxyService;

    public NettyClientInvoker() {
        init();
        this.outboundHandler = new NettyHttpClientOutboundHandler();
        int cores = Runtime.getRuntime().availableProcessors() * 2;
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);
    }

    @Override
    public FullHttpResponse invoke(FullHttpRequest fullRequest, ChannelHandlerContext ctx) throws Exception {
        ReferenceCountUtil.retain(fullRequest);

        proxyService.execute(() -> {
            try {
                URI uri = new URI(fullRequest.uri());
                ChannelFuture  f =connect(uri.getHost(), uri.getPort());
                fullRequest.setUri(uri.getPath());
                outboundHandler.setFullRequest(fullRequest);
                outboundHandler.setServerCtx(ctx);
                f.channel().writeAndFlush(fullRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    public void init() {
        workerGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast(new HttpResponseDecoder())   // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                        .addLast(new HttpRequestEncoder()) // 客户端发送的是httpRequest，所以要使用HttpRequestEncoder进行编码
                        .addLast(new HttpObjectAggregator(1024 * 10 * 1024));

                ch.pipeline().addLast(outboundHandler);

            }
        });
    }

    public void close(ChannelFuture f) throws InterruptedException {
        try {
            f.channel().close();
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully().sync();
            log.info("NettyHttpClient shutdownGracefully");
        }
    }

    public ChannelFuture connect(String host, int port) throws Exception {
        // Start the client.
        String remoteHost = host+":"+port;
        ChannelFuture channelFuture= channelMap.get(remoteHost);
        if(channelFuture==null){
            channelFuture = b.connect(host,port).sync();
            channelMap.put(remoteHost,channelFuture);
        }
        return channelFuture;

    }

}