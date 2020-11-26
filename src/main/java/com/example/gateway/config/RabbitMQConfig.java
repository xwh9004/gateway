package com.example.gateway.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>Description:</b>
 * <p><b>Company:</b>
 *
 * @author created by Jesse Xu at 17:29 on 2019/7/19
 * @version V0.1
 * @classNmae RabbitMQConfig
 */
@Configuration
public class RabbitMQConfig {

    public static final String REQUEST_EXCHANGE_NAME = "fanout.request";

    public static final String RESPONSE_EXCHANGE_NAME = "fanout.response";

    // 网关请求列表
    public static final String GATEWAY_REQUEST = "gateway_request";

    public static final String GATEWAY_RESPONSE = "gateway_response";

    @Bean("requestQueue")
    public Queue requestQueue(){
        return new Queue(GATEWAY_REQUEST, true,true,false);
    }
    @Bean("responseQueue")
    public Queue responseQueue(){
        return new Queue(GATEWAY_RESPONSE, true,true,false);
    }

    @Bean
    public Binding requestBinding() {

        return BindingBuilder.bind(requestQueue()).to(new FanoutExchange(REQUEST_EXCHANGE_NAME));

    }

    @Bean
    public Binding responseBinding() {
        return BindingBuilder.bind(responseQueue()).to(new FanoutExchange(RESPONSE_EXCHANGE_NAME));

    }

    // 定义消息转换器
    @Bean
    Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    // 定义消息模板用于发布消息，并且设置其消息转换器
    @Bean
    RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    RabbitAdmin rabbitAdmin(final ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
