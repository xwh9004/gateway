package com.example.gateway.jms;

import com.example.gateway.config.RabbitMQConfig;
import com.example.gateway.outbound.Invoker;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * <p><b>Description:</b>
 * TODO
 * <p><b>Company:</b>
 *
 * @author created by Jesse Hsu at 14:38 on 2020/11/25
 * @version V0.1
 * @classNmae MessageConsumer
 */
@Slf4j
@Component
public class MessageConsumer {

    @Autowired
    private Invoker invoker ;

    @RabbitListener(queues = RabbitMQConfig.GATEWAY_REQUEST)
    public void consume(RequestMessage request,Message message, Channel channel) throws Exception {



        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(
                HttpVersion.valueOf(request.getVersion()),
                HttpMethod.valueOf(request.getMethod()),
                request.getUrl());
        System.out.println("consume "+request);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        invoker.invoke(fullHttpRequest);
    }
}
