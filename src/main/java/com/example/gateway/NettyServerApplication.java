package com.example.gateway;


import com.example.gateway.inbound.HttpInboundServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *  http://localhost:8888/api/hello  ==> com.example.gateway API
 *  http://localhost:8801/api/hello  ==> backend service
 */
@SpringBootApplication(scanBasePackageClasses = NettyServerApplication.class)
public class NettyServerApplication {
    
    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "1.0.0";
    
    public static void main(String[] args) {


        SpringApplication.run(NettyServerApplication.class);

    }
}
