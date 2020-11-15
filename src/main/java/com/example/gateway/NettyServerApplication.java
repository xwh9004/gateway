package com.example.gateway;


import com.example.gateway.inbound.HttpInboundServer;

/**
 *  http://localhost:8888/api/hello  ==> com.example.gateway API
 *  http://localhost:8801/api/hello  ==> backend service
 */
public class NettyServerApplication {
    
    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "1.0.0";
    
    public static void main(String[] args) {
        String proxyServer = System.getProperty("proxyServer","http://localhost:8801");
        String proxyPort = System.getProperty("proxyPort","8888");
        int port = Integer.parseInt(proxyPort);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = new HttpInboundServer(port, proxyServer);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
