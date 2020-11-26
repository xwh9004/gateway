package com.example.gateway;


import com.example.gateway.config.RabbitMQConfig;
import com.example.gateway.inbound.HttpInboundServer;
import com.example.gateway.jms.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 *  http://localhost:8888/api/hello  ==> com.example.gateway API
 *  http://localhost:8801/api/hello  ==> backend service
 */
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass=true)  //enable aspect
@SpringBootApplication(scanBasePackageClasses = NettyServerApplication.class,exclude = DataSourceAutoConfiguration.class )
public class NettyServerApplication {



    public static void main(String[] args) {


        ConfigurableApplicationContext context = SpringApplication.run(NettyServerApplication.class);

    }
}
