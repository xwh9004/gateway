package com.example.gateway.jms;

import com.example.gateway.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
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
 * @author created by Jesse Hsu at 14:36 on 2020/11/25
 * @version V0.1
 * @classNmae MessageProducer
 */
@Slf4j
@Component
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void produce(FullHttpRequest fullRequest){

        String uri =fullRequest.uri();
        String method =fullRequest.method().name();
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setUrl(uri);
        requestMessage.setVersion(fullRequest.protocolVersion().text());
        requestMessage.setMethod(method);

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.REQUEST_EXCHANGE_NAME,RabbitMQConfig.GATEWAY_REQUEST, requestMessage, messageProcessor ->{

                messageProcessor.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return messageProcessor;
            });

        } catch (AmqpException e) {
            log.error("***********推送消息到 gateway_queue 出错  ************{}",e.getMessage());

        }
    }




}
